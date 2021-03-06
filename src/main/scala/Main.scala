import java.util.concurrent.atomic.AtomicBoolean

import rocks.xmpp.core.session.XmppClient
import rocks.xmpp.core.stanza.model.Message
import rocks.xmpp.core.XmppException
import rocks.xmpp.addr.Jid

import rocks.xmpp.extensions.receipts.{MessageDeliveryReceiptsManager => XEP0184}

object Main {
  var reacted = new AtomicBoolean(false)

  def xmppTry[T](expr: => T, desc: String): T = {
    try {
      expr
    } catch {
      case e: XmppException => {
        val msg = s"Error: ${desc}:\n${e.getMessage}"
        Console.err.println(msg)
        System.exit(1).asInstanceOf[T]
      }
    }
  }

  def main(args: Array[String]): Unit = { // scalastyle:ignore method.length
    if (args.length != 3) {
      Console.err.println(
        "Usage: xmppester remote-jid interval-minutes message")
      System.exit(1)
    }
    val remotejid = Jid.of(args(0))
    val interval = args(1).toLong * 60 * 1000
    val msgText = args(2)

    val settings = SettingsLoader.fromFile("xmppester.conf") match {
      case Right(pwd) => pwd
      case Left(e) => {
        val msg = e.toList.map(a => a.description).mkString(": ")
        Console.err.println(s"Error reading the settings: ${msg}")
        System.exit(1).asInstanceOf[Settings]
      }
    }

    Console.err.println(s"Will send the message to ${remotejid}...")
    val client = XmppClient.create(settings.server)
    client.getManager(classOf[XEP0184]).setEnabled(true)

    val mainThread = Thread.currentThread()
    client.addInboundMessageListener((event) => {
      val msg = event.getMessage
      val msgjid = msg.getFrom
      if ((remotejid.asBareJid == msgjid.asBareJid) && (msg.getBody != null)) {
        mainThread.interrupt()
        reacted set true
        Console.err.println(s"${remotejid} reacted, finishing")
      }
    })

    Console.err.println("Connecting...")
    xmppTry(client.connect(), "connection failed")
    Console.err.println("Logging in...")
    // TODO catch authorizationerrors
    xmppTry(
      client.login(settings.username, settings.password, "xmppester"),
      "login failed"
    )
    Console.err.println("Logged in!")
    val msg = new Message(remotejid, Message.Type.CHAT, msgText)

    // TODO investigate Actors multithreading instead
    while (!reacted.get()) {
      xmppTry(client send msg, "sending failed")
      try {
        Thread.sleep(interval)
      } catch {
        case _: InterruptedException => {}
      }
    }

    Console.err.println("Done, exiting")
    client.close() // FIXME do that in finally
  }
}
