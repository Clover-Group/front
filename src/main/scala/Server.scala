package tsp

//import cats.effect._
import cats.effect.{ConcurrentEffect, Effect, ExitCode, IO, IOApp, Timer, ContextShift}
import cats.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import fs2.Stream
import scala.concurrent.ExecutionContext.global

// Server components
import config._
import db.Database

object Server {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {

    for {
      // Read global config
      cfg <- Stream.eval(Config.load[F]())
      
      // Config DBMS
      //root = new Database.ServerTransactor[F]
      //transactor <- Stream.eval(root.create(cfg.db))
      //_ <- Stream.eval(root.init(transactor))
      _ <- Stream.eval(Database.dummy)

      // Build HTTP client
      client <- BlazeClientBuilder[F](global).stream
      helloWorldAlg = HelloWorld.impl[F]
      jokeAlg = Jokes.impl[F](client)

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      httpApp = (
        Routes.helloWorldRoutes[F](helloWorldAlg) <+>
        Routes.jokeRoutes[F](jokeAlg)
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- BlazeServerBuilder[F]
        .bindHttp(cfg.server.port, cfg.server.host)
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
