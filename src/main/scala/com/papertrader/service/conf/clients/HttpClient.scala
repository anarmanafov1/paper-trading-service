package com.papertrader.service.conf.clients

import cats.MonadError
import cats.implicits._
import com.papertrader.service._
import org.http4s.{EntityDecoder, Status, Uri}
import org.http4s.client.Client
import org.typelevel.log4cats.Logger

trait HttpClient {
  val baseUrl: Uri

  def get[F[_], A](uri: Uri)(implicit client: Client[F], entityDecoder: EntityDecoder[F, A], logger: Logger[F], me: MonadError[F, Throwable]): F[A] = {
    val uriForLog = uri.withQueryParam("apikey", "####").renderString
    client.get[A](uri) {
      case Status.Successful(r) =>
        for {
          _ <- logger.error(s"Got OK response for request: $uriForLog")
          parsed <- r.as[A].handleErrorWith(_ => me.raiseError(StockClientParseError))
        } yield parsed
      case r if r.status == Status.NotFound =>
        logger.error(s"Got NotFound response for request with api key removed: $uriForLog, responding with $StockClientNotFoundError")
          .flatMap(_ => me.raiseError(StockClientNotFoundError))
      case v =>
        logger.error(s"Unhandled response with status ${v.status}, responding with $StockClientServerError")
          .flatMap(_ => me.raiseError(StockClientServerError))
    }
  }
}
