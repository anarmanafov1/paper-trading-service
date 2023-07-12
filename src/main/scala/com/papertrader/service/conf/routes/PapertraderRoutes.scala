package com.papertrader.service.conf.routes

import cats.MonadError
import cats.data.NonEmptyList
import cats.effect.Async
import cats.effect.kernel.Concurrent
import cats.implicits.{catsSyntaxApplicativeError, catsSyntaxFlatMapOps, toFlatMapOps}
import com.papertrader.service._
import com.papertrader.service.models.Item
import io.circe.generic.auto.exportEncoder
import io.circe.syntax.EncoderOps
import org.http4s.{Header, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import org.typelevel.log4cats.Logger
import io.circe.generic.auto._
import org.typelevel.ci.CIString
import cats.implicits._
import com.papertrader.service.util.controllers.ErrorResponse
import java.util.UUID
import scala.util.Try

object PapertraderRoutes {

  def routes[F[_]: Async](stockService: StockService[F])(implicit me: MonadError[F, Throwable], logger: Logger[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "global-quote" / symbol =>
        stockService.getGlobalQuote(symbol).attempt.flatMap {
          case Right(v) => Ok(v.asJson)
          case Left(StockClientNotFoundError) => NotFound(s"Stock with symbol $symbol not found.")
          case Left(StockClientServerError) => ServiceUnavailable("Failed to retrieve stock.")
          case Left(StockClientParseError) => InternalServerError("Error processing stock.")
          case Left(e: Throwable) => logger.error(s"Unhandles error with message: ${e.getMessage}") >> InternalServerError("Something went wrong.")
        }

      case r@POST -> Root / "cart" => {
        for {
          json <- r.asJson
          item <- me.fromEither(json.as[Item].left.map(e => MalformedBody(s"Body malformed, msg: ${e.getMessage()}")))
          userHeader: Option[NonEmptyList[Header.Raw]] = r.headers.get(CIString("user-id"))
          userIdRaw <- me.fromEither(userHeader.map(_.head).toRight(MissingHeaderError("header user-id not found")))
          userId <- me.fromTry(Try(UUID.fromString(userIdRaw.value))).adaptError(e => InvalidHeaderError(s"User Id header provided not valid UUID - msg: ${e.getMessage}"))
          _ <- stockService.addToBasket(item, userId)
          r <- NoContent()
        } yield r
      }.handleErrorWith {
        case e: MalformedBody => logger.error(e.msg) *> BadRequest(ErrorResponse(e.msg).asJson)
        case e: MissingHeaderError => logger.error(e.msg) *> BadRequest(ErrorResponse(e.msg).asJson)
        case e: InvalidHeaderError => logger.error(e.msg) *> BadRequest(ErrorResponse(e.msg).asJson)
        case e => logger.error(s"Unhandled error with msg: ${e.getMessage}, responding with InternalServerError") *>
          InternalServerError(ErrorResponse("Something went wrong on our side.").asJson)
      }
    }
  }
}