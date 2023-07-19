package com.papertrader.service.conf

import cats.MonadError
import cats.effect.Ref
import cats.implicits._
import com.papertrader.service._
import com.papertrader.service.util.clients.StockClient
import com.papertrader.service.util.controllers.ErrorResponse
import com.papertrader.service.util.controllers.RequestValidation.{validateBodyAsItem, validateUserIdHeader}
import io.circe.generic.auto.exportEncoder
import io.circe.syntax.EncoderOps
import org.http4s.HttpRoutes
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
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "global-quote" / symbol =>
        StockService
          .getGlobalQuote(symbol)
          .flatMap(v => Ok(v.asJson))
          .handleErrorWith {
            case HttpClientNotFoundError =>
              NotFound(
                ErrorResponse(s"Stock with symbol $symbol not found.").asJson
              )
            case HttpClientParseError =>
              InternalServerError(
                ErrorResponse("Error processing stock.").asJson
              )
            case _: HttpClientServerError =>
              ServiceUnavailable(
                ErrorResponse("Failed to retrieve stock.").asJson
              )
            case e: Throwable =>
              logger.error(
                s"Unhandles error with message: ${e.getMessage}"
              ) *> InternalServerError(
                ErrorResponse("Something went wrong.").asJson
              )
          }

      case r @ POST -> Root / "basket" =>
        {
          for {
            item <- validateBodyAsItem(r)
            userId <- validateUserIdHeader(r)
            _ <- StockService.addToBasket(item, userId)
            r <- Created()
          } yield r
        }.handleErrorWith {
          case e: MalformedBodyError =>
            logger.error(e.msg) *> BadRequest(ErrorResponse(e.msg).asJson)
          case e: MissingHeaderError =>
            logger.error(e.msg) *> BadRequest(ErrorResponse(e.msg).asJson)
          case e: InvalidHeaderError =>
            logger.error(e.msg) *> BadRequest(ErrorResponse(e.msg).asJson)
          case e =>
            logger.error(
              s"Unhandled error with msg: ${e.getMessage}, responding with InternalServerError"
            ) *>
              InternalServerError(
                ErrorResponse("Something went wrong on our side.").asJson
              )
        }

      case r @ GET -> Root / "basket" =>
        {
          for {
            userId <- validateUserIdHeader(r)
            basket <- StockService.viewBasket(userId)(me, basketRef)
            r <- Ok(basket.asJson)
          } yield r
        }.handleErrorWith {
          case e: MissingHeaderError =>
            logger.error(e.msg) *> BadRequest(ErrorResponse(e.msg).asJson)
          case e: InvalidHeaderError =>
            logger.error(e.msg) *> BadRequest(ErrorResponse(e.msg).asJson)
          case e =>
            logger.error(
              s"Unhandled error with msg: ${e.getMessage}, responding with InternalServerError"
            ) *>
              InternalServerError(
                ErrorResponse("Something went wrong on our side.").asJson
              )
        }
    }
  }
}
