package com.papertrader.service

import cats.effect.{Async, Ref, Resource}
import com.comcast.ip4s._
import com.papertrader.service.conf.ApplicationConfig
import com.papertrader.service.conf.routes.PapertraderRoutes
import org.http4s.client.{Client, JavaNetClientBuilder}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import com.papertrader.service.conf.logger.ApplicationLogger
import java.util.UUID

object Bootstrap {

  //  [error]  found   : F[cats.effect.kernel.Ref[F,Map[java.util.UUID,Map[String,Int]]]]
  //  [error]  required: cats.effect.Ref[F,Map[java.util.UUID,Map[String,Int]]]
  def bootstrap[F[+_]: Async]: Resource[F, Unit] = {
    for {
      implicit0(appConf: ApplicationConfig) <- Resource.eval(ApplicationConfig.load())
      implicit0(client: Client[F]) <- Resource.pure(JavaNetClientBuilder[F].create)
      implicit0(logger: org.typelevel.log4cats.Logger[F]) <- Resource.pure(ApplicationLogger.getLogger[F])
      implicit0(basketRef: Ref[F, Map[UUID, Map[String, Int]]]) <- Resource.pure(Ref.of[F, Map[UUID, Map[String, Int]]](Map.empty))
      stockService = new StockService[F]()
      _ <-
        EmberServerBuilder.default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(PapertraderRoutes.routes(stockService).orNotFound)
          .build
    } yield ()
  }

}
