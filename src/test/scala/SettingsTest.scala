import java.nio.file.Paths
import org.scalatest._

class SettingsSpec extends FlatSpec {
  "Settings" should "get loaded from config" in {
    val src = Paths.get(getClass.getResource("example.conf").getPath)
    val set = SettingsLoader.fromPath(src)
    val ok = new Settings("uuuu", "ssss", "pppp")
    assert(Right(ok) === set)
  }
}
