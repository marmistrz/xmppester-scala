import rocks.xmpp
import rocks.xmpp.core.session.XmppClient
import rocks.xmpp.core.XmppException

object Main extends App {
    if (args.length != 1) {
        println("Need at least one argument")
        System.exit(1)
    }
    val password = args(0)
    println("Connecting...")
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
}
