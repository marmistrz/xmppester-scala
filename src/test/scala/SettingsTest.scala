import util.Success
import org.scalatest._

class ExampleSpec extends FlatSpec {
  "Settings" should "get loaded from config" in {
    val str = """
    {
      "server": "srv",
      "username": "usr",
      "password": "pwd"
    }
    """
    val conf = new Settings(username = "usr", server = "srv", password = "pwd")
    assert(SettingsLoader.fromString(str) === Success(conf))
  }
}
