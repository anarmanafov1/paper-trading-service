package com.papertrader.service.conf

import cats.MonadError
import cats.effect.Ref
import cats.implicits._
import com.papertrader.service._
import com.papertrader.service.util.clients.StockClient
import com.papertrader.service.util.controllers.ErrorResponse
import com.papertrader.service.util.controllers.RequestValidation.{
  validateBodyAsItem,
  validateUserIdHeader
}
import io.circe.generic.auto.exportEncoder
import io.circe.syntax.EncoderOps
import org.http4s.{HttpRoutes, Response}
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger

import java.util.UUID

object Routes {

  def all[F[_]]()(implicit
      logger: Logger[F],
      me: MonadError[F, Throwable],
      jsonDecoder: JsonDecoder[F],
      client: Client[F],
      appConf: ApplicationConfig,
      stockClient: StockClient[F],
      basketRef: Ref[F, Map[UUID, Map[String, Int]]]
  ): HttpRoutes[F] = {
    implicit val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "global-quote" / symbol =>
        StockService
          .getGlobalQuote(symbol)
          .flatMap(v => Ok(v.asJson))
          .handleErrorWith(errorHandler(_))

      case r @ POST -> Root / "basket" =>
        {
          for {
            item <- validateBodyAsItem(r)
            userId <- validateUserIdHeader(r)
            _ <- StockService.addToBasket(item, userId)
            r <- Created()
          } yield r
        }.handleErrorWith(errorHandler(_))

      case r @ GET -> Root / "basket" =>
        {
          for {
            userId <- validateUserIdHeader(r)
            basket <- StockService.viewBasket(userId)(me, basketRef)
            r <- Ok(basket.asJson)
          } yield r
        }.handleErrorWith(errorHandler(_))
    }
  }

  def errorHandler[F[_]](e: Throwable)(implicit
      logger: Logger[F],
      me: MonadError[F, Throwable],
      dsl: Http4sDsl[F]
  ): F[Response[F]] = {
    import dsl._
    e match {
      case StockNotFoundError(symbol) =>
        logger.info(
          "Stock not found, returning not found response"
        ) *> NotFound(
          ErrorResponse(s"Stock with symbol $symbol not found.").asJson
        )
      case StockClientInternalError(msg) =>
        logger.error(
          s"Stock client error with msg: $msg, returning error response"
        ) *> InternalServerError(
          ErrorResponse("Error processing stock").asJson
        )
      case _: HttpClientServerError =>
        logger.error(
          "Stock client error, returning error response"
        ) *> InternalServerError(
          ErrorResponse("Failed to retrieve stock.").asJson
        )
      case MalformedBodyError(msg) =>
        logger.error(msg) *> BadRequest(ErrorResponse(msg).asJson)
      case MissingHeaderError(msg) =>
        logger.error(msg) *> BadRequest(ErrorResponse(msg).asJson)
      case InvalidHeaderError(msg) =>
        logger.error(msg) *> BadRequest(ErrorResponse(msg).asJson)
      case RefReadError(msg) =>
        logger.error(
          s"Error writing to ref basket, msg: $msg"
        ) *> InternalServerError(ErrorResponse(msg).asJson)
      case RefWriteError(msg) =>
        logger.error(
          s"Error reading from ref basket, msg: $msg"
        ) *> InternalServerError(ErrorResponse(msg).asJson)
      case e: Throwable =>
        logger.error(
          s"Unhandled error with msg: ${e.getMessage}, responding with InternalServerError"
        ) *>
          InternalServerError(
            ErrorResponse("Something went wrong on our side.").asJson
          )
    }
  }
}
