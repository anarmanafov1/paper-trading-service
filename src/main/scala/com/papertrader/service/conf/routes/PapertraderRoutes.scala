package com.papertrader.service.conf.routes

import cats.effect.IO
import com.papertrader.service.StockService
import com.papertrader.service.conf.ApplicationConfig
import io.circe.generic.auto.exportEncoder
import io.circe.syntax.EncoderOps
import org.http4s.HttpRoutes
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._

object PapertraderRoutes {

  def routes()(implicit client: Client[IO], appConf: ApplicationConfig): HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO]{}
    import dsl._
    HttpRoutes.of[IO] {
      case GET -> Root / "global-quote" / symbol =>
        StockService.getGlobalQuote(symbol).flatMap {
          case Left(e) => InternalServerError(e)
          case Right(v) => Ok(v.asJson)
        }
    }
  }
}