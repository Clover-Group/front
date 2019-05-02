
import cats.effect.IO

import pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.catseffect._


package object config {

  case class ServerConfig(host: String ,port: Int)

  case class DatabaseConfig(driver: String, url: String, user: String, password: String)

  case class Config(server: ServerConfig, database: DatabaseConfig)

  object Config {

    def load(configFile: String = "application.conf"): IO[Config] = loadConfigF[IO,Config](configFile)

  }
}
