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

  def transactor[F[_]:ConcurrentEffect](cfg: DatabaseConfig)(implicit T: Timer[F], C: ContextShift[F]): Resource[F,HikariTransactor[F]] = for {
    ce <- ExecutionContexts.fixedThreadPool[F](cfg.threads)
    te <- ExecutionContexts.cachedThreadPool[F]
    xa <- HikariTransactor.newHikariTransactor[F](cfg.driver, cfg.url, cfg.user, cfg.password, ce, te)
  } yield xa


  def initialize(transactor: HikariTransactor[IO]): IO[Unit] = {
    transactor.configure { dataSource =>
      IO {
        val flyWay = Flyway.configure().dataSource(dataSource).load()
        flyWay.migrate()
        ()
      }
    }
  }

  //def run(args: List[String]): IO[ExitCode] =
  //  transactor.use { xa =>
  //    FirstExample.examples.transact(xa).as(ExitCode.Success)
  //}

  //def initialize[F[_]:ConcurrentEffect](transactor: HikariTransactor[F]): F[Unit] = {
  //  transactor.configure { dataSource =>
  //    //IO.pure {
  //      val flyWay = Flyway.configure().dataSource(dataSource).load()
  //      flyWay.migrate()
  //      ()
  //    //}
  //  }
  //}

}
