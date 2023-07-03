package com.papertrader

import cats.effect.{IO, IOApp}
import cats.effect.Resource
import com.comcast.ip4s._
import com.papertrader.routes.PapertraderRoutes
import org.http4s.client.JavaNetClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
//import org.http4s.server.middleware.Logger

object Main extends IOApp.Simple {
  val run = {
    for {
      _ <- Resource.pure(JavaNetClientBuilder[IO].create)

      // TODO: Move to routes
      httpApp = (
        PapertraderRoutes.routes[IO]
        ).orNotFound

      // With Middlewares in place
//      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      _ <-
        EmberServerBuilder.default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(httpApp)
          .build
    } yield ()
  }.useForever
}
