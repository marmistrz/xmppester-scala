import scala.util.Try
import spray.json._

case class Settings(username: String, server: String, password: String) {}

object SettingsJsonProtocol extends DefaultJsonProtocol {
  implicit val settingsFormat = jsonFormat3(Settings)
}

object SettingsLoader {
  import SettingsJsonProtocol._

  def fromString(str: String): Try[Settings] = {
    Try(str.parseJson.convertTo[Settings])
  }

  def fromFile(file: String): Try[Settings] = {
    Try {
      val source = io.Source.fromFile(file)
      val contents =
        try source.getLines mkString "\n"
        finally source.close()
      contents
    }.flatMap(fromString)
  }
}
