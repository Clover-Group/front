package db

import cats.effect._
import cats.implicits._

import config.DatabaseConfig
import org.flywaydb.core.Flyway
import doobie._
import doobie.hikari._
import doobie.implicits._

import config._




object Database {

  class ServerTransactor[F[_]:ConcurrentEffect] {
  
    type HikariResource = Resource[F, HikariTransactor[F]]

    def create (cfg: DatabaseConfig)(implicit T: Timer[F], C: ContextShift[F]):HikariResource = for {
      ce <- ExecutionContexts.fixedThreadPool[F](cfg.threads)
      te <- ExecutionContexts.cachedThreadPool[F]
      xa <- HikariTransactor.newHikariTransactor[F](cfg.driver, cfg.url, cfg.user, cfg.password, ce, te)
    } yield xa 

    def init(src:HikariResource): F[Unit] = {
      ConcurrentEffect[F].unit
    }


  }

}
