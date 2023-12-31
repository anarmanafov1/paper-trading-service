package com.papertrader.service

import cats.effect.{Async, Ref, Resource}
import com.comcast.ip4s._
import com.papertrader.service.conf.{ApplicationConfig, Routes}
import org.http4s.client.{Client, JavaNetClientBuilder}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import com.papertrader.service.util.clients.StockClient
import com.papertrader.service.util.logger.ApplicationLogger
import org.typelevel.log4cats.Logger
import java.util.UUID

object Bootstrap {
  def bootstrap[F[_]: Async]: Resource[F, Unit] = {
    implicit val client: Client[F] = JavaNetClientBuilder[F].create
    implicit val logger: Logger[F] = ApplicationLogger.getLogger[F]
    implicit val stockClient: StockClient[F] =
      new StockClient[F] {}

    for {
      implicit0(appConf: ApplicationConfig) <- Resource.eval(
        ApplicationConfig.load()
      )
      implicit0(basketRef: Ref[F, Map[UUID, Map[String, Int]]]) <- Resource
        .eval(Ref.of[F, Map[UUID, Map[String, Int]]](Map.empty))
      _ <-
        EmberServerBuilder
          .default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(Routes.all().orNotFound)
          .build
    } yield ()
  }

}
