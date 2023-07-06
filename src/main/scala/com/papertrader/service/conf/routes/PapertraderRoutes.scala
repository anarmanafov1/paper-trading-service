package com.papertrader.service.conf.routes

import cats.MonadError
import cats.implicits.catsSyntaxFlatMapOps
import com.papertrader.service._
import io.circe.generic.auto.exportEncoder
import io.circe.syntax.EncoderOps
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._

object PapertraderRoutes {

  def routes[F[_]](stockService: StockService[F])(implicit me: MonadError[F, Throwable], logger: org.typelevel.log4cats.Logger[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "global-quote" / symbol =>
        me.flatMap(me.attempt(stockService.getGlobalQuote(symbol))) {
          case Right(v) => Ok(v.asJson)
          case Left(StockClientNotFoundError) => NotFound(s"Stock with symbol $symbol not found.")
          case Left(StockClientServerError) => ServiceUnavailable("Failed to retrieve stock.")
          case Left(StockClientParseError) => InternalServerError("Error processing stock.")
          case Left(e: Throwable) =>
            e.printStackTrace()
            logger.error(e.getMessage) >> InternalServerError("Something went wrong.")
        }
    }
  }
}