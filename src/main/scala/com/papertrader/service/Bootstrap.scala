package com.papertrader.service

import cats.effect.{IO, Resource}
import com.comcast.ip4s._
import com.papertrader.service.conf.ApplicationConfig
import com.papertrader.service.conf.routes.PapertraderRoutes
import org.http4s.client.{Client, JavaNetClientBuilder}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import com.papertrader.service.conf.logger.ApplicationLogger
object Bootstrap {
  def all: IO[Unit] = {
    for {
      implicit0(appConf: ApplicationConfig) <- Resource.eval(
        IO.fromEither(
          ApplicationConfig
            .load
            .left
            .map(e => new Throwable(e.prettyPrint()))
        )
      )
      implicit0(l: org.typelevel.log4cats.Logger[IO]) <- Resource.pure(ApplicationLogger.getLogger[IO])
      implicit0(c: Client[IO]) <- Resource.pure(JavaNetClientBuilder[IO].create)

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
