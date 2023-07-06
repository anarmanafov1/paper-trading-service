package com.papertrader.service

import cats.effect.{Async, Resource}
import com.comcast.ip4s._
import com.papertrader.service.conf.ApplicationConfig
import com.papertrader.service.conf.routes.PapertraderRoutes
import org.http4s.client.{Client, JavaNetClientBuilder}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import com.papertrader.service.conf.logger.ApplicationLogger

object Bootstrap {
  def bootstrap[F[+_]: Async]: Resource[F, Unit] = {
    for {
      implicit0(ac: ApplicationConfig) <- Resource.eval(ApplicationConfig.load())
      implicit0(c: Client[F]) <- Resource.pure(JavaNetClientBuilder[F].create)
      implicit0(l: org.typelevel.log4cats.Logger[F]) <- Resource.pure(ApplicationLogger.getLogger[F])
      _ <-
        EmberServerBuilder.default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(PapertraderRoutes.routes().orNotFound)
          .build
    } yield ()
  }

}
