import scala.io

import java.util.concurrent.atomic.AtomicBoolean

import rocks.xmpp.core.session.XmppClient
import rocks.xmpp.core.stanza.model.Message
import rocks.xmpp.core.XmppException
import rocks.xmpp.addr.Jid

object Main {
  val password = {
    val source = io.Source.fromFile("password.txt")
    // FIXME some better error handling
    val passwd = try source.getLines.next
    finally source.close
    passwd
  }

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

  def main(args: Array[String]): Unit = {

    if (args.length != 2) {
      Console.err.println("Usage: xmppester remote-jid interval-ms")
      System.exit(1)
    }
    val remotejid = Jid.of(args(0))
    val interval = args(1).toLong

    Console.err.println(s"Will send the message to ${remotejid}...")
    // TODO load from toml
    val client = XmppClient.create("wiuwiu.de")
    client.addInboundMessageListener((event) => {
      val msg = event.getMessage
      val msgjid = msg.getFrom
      if ((remotejid.asBareJid == msgjid.asBareJid) && (msg.getBody != null)) {
        reacted set true
        Console.err.println(s"${remotejid} reacted, finishing")
      }
    })

    Console.err.println("Connecting...")
    xmppTry(client.connect(), "connection failed")
    Console.err.println("Logging in...")
    // TODO load from toml
    // TODO catch authorizationerrors
    xmppTry(client.login("marmistrz-bot", password, "rocks"), "login failed")
    Console.err.println("Logged in!")
    // TODO load from args
    val msg = new Message(remotejid, Message.Type.CHAT, "test")

    while (!reacted.get()) {
      xmppTry(client send msg, "sending failed")
      Thread.sleep(interval)
    }

    Console.err.println("Done, exiting")
    client.close() // FIXME do that in finally
  }
}
