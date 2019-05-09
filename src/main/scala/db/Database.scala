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

  // this works
  def dummy[F[_]:Sync] (): F[Unit] = {
    Sync[F].unit
  }

  // Concurrent Effect Won't work
  class ServerTransactor[F[_]:ConcurrentEffect] {
  
    type HikariResource = Resource[F, HikariTransactor[F]]

    def create (cfg: DatabaseConfig)(implicit T: Timer[F], C: ContextShift[F]):HikariResource = for {
      ce <- ExecutionContexts.fixedThreadPool[F](cfg.threads)
      te <- ExecutionContexts.cachedThreadPool[F]
      xa <- HikariTransactor.newHikariTransactor[F](cfg.driver, cfg.url, cfg.user, cfg.password, ce, te)
    } yield xa 

    def init (trans:HikariTransactor[F]): F[Unit] = {
      trans.configure ( dataSource =>
        ConcurrentEffect[F].pure {
          Flyway.configure().dataSource(dataSource).load().migrate()
        }
      )
    }
    

  }
}

