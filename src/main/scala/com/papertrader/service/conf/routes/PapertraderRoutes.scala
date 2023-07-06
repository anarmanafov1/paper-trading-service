package com.papertrader.service.conf.routes

import cats.MonadError
import com.papertrader.service._
import io.circe.generic.auto.exportEncoder
import io.circe.syntax.EncoderOps
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._

object PapertraderRoutes {

  def routes[F[_]](stockService: StockService[F])(implicit me: MonadError[F, Throwable]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "global-quote" / symbol =>
        me.flatMap(me.attempt(stockService.getGlobalQuote(symbol))) {
          case Left(StockClientNotFoundError) => NotFound(s"Stock with symbol $symbol not found.")
          case Left(StockClientServerError | StockClientParseError) => InternalServerError("Something went wrong.")
          case Right(v) => Ok(v.asJson)
          case _ =>
            println("suppress errors") //TODO: Fix
            NoContent()
        }
    }
  }
}