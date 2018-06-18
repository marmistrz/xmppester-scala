import scala.io

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

  def xmppTry[T](expr: => T, desc: String): T = {
    try {
      expr
    } catch {
      case e: XmppException => {
        val msg = s"Error: ${desc}:\n${e.getMessage}"
        println(msg)
        System.exit(1).asInstanceOf[T]
      }
    }
  }

  def main(args: Array[String]): Unit = {

    if (args.length != 1) {
      Console.err.println("Usage: xmppester remote-jid")
      System.exit(1)
    }
    val remotejid = args(0)

    println(s"Will send the message to ${remotejid}...")
    val client = XmppClient.create("wiuwiu.de")

    xmppTry(client.connect(), "connection failed")
    xmppTry(client.login("marmistrz", password, "rocks"), "login failed")
    val msg = new Message(Jid.of(remotejid), Message.Type.CHAT, "test")
    xmppTry(client send msg, "sending failed")

    println("Done")
  }
}
