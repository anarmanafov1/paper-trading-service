package com.papertrader.service

import cats.effect.{IO, Resource}
import com.comcast.ip4s._
import com.papertrader.service.conf.ApplicationConfig
import com.papertrader.service.conf.routes.PapertraderRoutes
import org.http4s.client.{Client, JavaNetClientBuilder}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import com.papertrader.service.conf.logger.ApplicationLogger
import org.typelevel.log4cats.Logger

object Bootstrap {
  def all: IO[Unit] = {
    for {
      implicit0(l: org.typelevel.log4cats.Logger[IO]) <- Resource.pure(ApplicationLogger.getLogger[IO])
      implicit0(appConf: ApplicationConfig) <- loadApplicationConfig()
      implicit0(c: Client[IO]) <- Resource.pure(JavaNetClientBuilder[IO].create)
      _ <-
        EmberServerBuilder.default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(PapertraderRoutes.routes().orNotFound)
          .build
    } yield ()
  }.useForever

  private def loadApplicationConfig()(implicit logger: Logger[IO]): Resource[IO, ApplicationConfig] =
    Resource.eval(
      IO.fromEither(
        ApplicationConfig
          .load
          .left
          .map(e => new Throwable(e.prettyPrint()))
      ).handleErrorWith(e => logger.error(e.getMessage) >> IO.raiseError(e)))
}
