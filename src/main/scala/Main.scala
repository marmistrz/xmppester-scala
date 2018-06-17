import rocks.xmpp
import rocks.xmpp.core.session.XmppClient
import rocks.xmpp.core.XmppException

object Main extends App {
  println("Connecting...")
  val password = ""
  val client = XmppClient.create("wiuwiu.de")
  try {
    client.connect()
  } catch {
    case _: XmppException => {
      println("Connection failed")
      System.exit(1)
    }
  }
  try {
    client.login("marmistrz", password, "rocks")
  } catch {
    case _: XmppException => {
      println("login failed")
      System.exit(1)
    }
  }

  println("Done")
}
