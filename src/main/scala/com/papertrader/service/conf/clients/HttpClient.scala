package com.papertrader.service.conf.clients

import cats.{Applicative, FlatMap}
import cats.effect.Async
import cats.implicits.{catsSyntaxFlatMapOps}
import com.papertrader.service.{StockClientError, StockClientNotFoundError, StockClientParseError, StockClientServerError}
import io.circe.Decoder
import org.http4s.{Status, Uri}
import org.http4s.client.Client
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.typelevel.log4cats.Logger

trait HttpClient {
  val baseUrl: Uri

  def get[F[+_]: Async, A](uri: Uri)(implicit client: Client[F], decoder: Decoder[A], logger: Logger[F], ap: Applicative[F]): F[Either[StockClientError, A]] = {
    val uriForLog = uri.withQueryParam("apikey", "####").renderString
    client.get[Either[StockClientError, A]](uri) {
      case Status.Successful(r) =>
        logger.error(s"Got OK response for request: $uriForLog") >> r.attemptAs[A].leftMap(_ => StockClientParseError).value
      case r if r.status == Status.NotFound =>
        logger.error(s"Got NotFound response for request with api key removed: $uriForLog, responding with $StockClientNotFoundError") >> ap.pure(Left(StockClientNotFoundError))
      case v => logger.error(s"Unhandled response with status ${v.status}, responding with $StockClientServerError") >> ap.pure(Left(StockClientServerError))
    }
  }
}
