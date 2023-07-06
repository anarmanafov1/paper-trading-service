package com.papertrader.service.conf.routes

import cats.effect.Async
import cats.implicits.toFlatMapOps
import com.papertrader.service.{StockClientNotFoundError, StockClientParseError, StockClientServerError, StockService}
import com.papertrader.service.conf.ApplicationConfig
import io.circe.generic.auto.exportEncoder
import io.circe.syntax.EncoderOps
import org.http4s.HttpRoutes
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import org.typelevel.log4cats.Logger

object PapertraderRoutes {

  def routes[F[+_]: Async]()(implicit client: Client[F], appConf: ApplicationConfig, logger: Logger[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "global-quote" / symbol =>
        StockService.getGlobalQuote(symbol).flatMap {
          case Left(StockClientNotFoundError) => NotFound(s"Stock with symbol $symbol not found.")
          case Left(StockClientServerError | StockClientParseError) => InternalServerError("Something went wrong.")
          case Right(v) => Ok(v.asJson)
        }
    }
  }
}