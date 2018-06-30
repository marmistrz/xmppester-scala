import pureconfig.loadConfig
import pureconfig.error.ConfigReaderFailures
import java.nio.file.{Path, Paths}

case class Settings(username: String, server: String, password: String) {}

object SettingsLoader {
  type SettingsEither = Either[ConfigReaderFailures, Settings];

  def fromFile(file: String): SettingsEither = {
    fromPath(Paths.get(file))
  }

  def fromPath(path: Path): SettingsEither = {
    loadConfig[Settings](path = path)
  }
}
