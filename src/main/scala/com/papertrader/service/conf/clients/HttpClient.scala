package com.papertrader.service.conf.clients

import cats.MonadError
import com.papertrader.service._
import org.http4s.{EntityDecoder, Status, Uri}
import org.http4s.client.Client
import org.typelevel.log4cats.Logger

trait HttpClient {
  val baseUrl: Uri

  def get[F[_], A](uri: Uri)(implicit client: Client[F], entityDecoder: EntityDecoder[F, A], logger: Logger[F], me: MonadError[F, Throwable]): F[A] = {
    val uriForLog = uri.withQueryParam("apikey", "####").renderString
    client.get[A](uri) {
      case Status.Successful(r) => me.flatMap(logger.error(s"Got OK response for request: $uriForLog"))(_ => me.flatMap(r.as[A])(_ => me.raiseError(StockClientParseError)))
      case r if r.status == Status.NotFound => me.flatMap(logger.error(s"Got NotFound response for request with api key removed: $uriForLog, responding with $StockClientNotFoundError"))(_ => me.raiseError(StockClientNotFoundError))
      case v => me.flatMap(logger.error(s"Unhandled response with status ${v.status}, responding with $StockClientServerError"))(_ => me.raiseError(StockClientServerError))
    }
  }
}
