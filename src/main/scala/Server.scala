package tsp

import cats.effect.{ConcurrentEffect, Effect, ExitCode, IO, IOApp, Timer, ContextShift}
import cats.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import fs2.Stream
import scala.concurrent.ExecutionContext.global

import config.Config


object Server {

  //val cfg = Stream.eval(Config.load())
  //config.shit()

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
    for {
      
      cfg[F, Nothing] <- Stream.eval(Config.load())
      port = cfg.server.port 
      //_ <- port.hs
      host = cfg.server.host
      //_ <- host.hs

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
        .bindHttp(port.toInt, host.toString())
        //.bindHttp(cfg.server.port, cfg.server.host)
        //.bindHttp(8080, "127.0.0.1")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
