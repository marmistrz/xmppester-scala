import scala.io
import scala.util.{Failure, Success, Try}

import java.util.concurrent.atomic.AtomicBoolean

import rocks.xmpp.core.session.XmppClient
import rocks.xmpp.core.stanza.model.Message
import rocks.xmpp.core.XmppException
import rocks.xmpp.addr.Jid

object Main {
  implicit class IteratorPlus[T](i: Iterator[T]) {
    def nextOption: Option[T] = if (i.hasNext) Some(i.next()) else None
  }

  private def readPassword: Try[String] = {
    Try {
      val source = io.Source.fromFile("password.txt")
      val pwdOption =
        try source.getLines.nextOption
        finally source.close()
      pwdOption match {
        case Some(p) => p
        case None =>
          throw new Exception("file needs to contain at least one line")
      }
    }
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

    val password = readPassword match {
      case Success(pwd) => pwd
      case Failure(e) => {
        Console.err.println(s"Error reading the password: ${e.getMessage}")
        System.exit(1).asInstanceOf[String]
      }
    }

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
