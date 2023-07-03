package com.papertrader

import cats.effect.{IO, IOApp}
import cats.effect.Resource
import com.comcast.ip4s._
import com.papertrader.service.conf.ApplicationConfig
import com.papertrader.service.conf.routes.PapertraderRoutes
import org.http4s.client.{Client, JavaNetClientBuilder}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._

object Main extends IOApp.Simple {
  val run: IO[Nothing] = {
    for {
      // TODO: Correctly evaluate resources
      implicit0(appconf: ApplicationConfig) <- Resource.eval(IO.fromEither(ApplicationConfig.load.left.map(e => new Throwable(e.prettyPrint()))))
      implicit0(c: Client[IO]) = JavaNetClientBuilder[IO].create

      // TODO: Move to routes
      httpApp = (
        PapertraderRoutes.routes()
        ).orNotFound

      _ <-
        EmberServerBuilder.default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(httpApp)
          .build
    } yield ()
  }.useForever
}
