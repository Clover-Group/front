
import cats.effect.Sync
import java.nio.file.{Path, Paths}
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.catseffect._


package object config {

  case class ServerConfig(host: String ,port: Int)

  case class DatabaseConfig(driver: String, url: String, user: String, password: String)

  case class Config(server: ServerConfig, database: DatabaseConfig)

  object Config {

    val configFile  = Paths.get ("./src/main/resources/application.conf")

    def load[F[_]: Sync](path: Path = configFile): F[Config] = loadConfigF[F,Config](path)

  }
}
