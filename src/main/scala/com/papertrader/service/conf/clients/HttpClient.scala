package com.papertrader.service.conf.clients

import cats.effect.{Async, IO}
import com.papertrader.service.{StockClientError, StockClientNotFoundError, StockClientParseError, StockClientServerError}
import io.circe.Decoder
import org.http4s.{Status, Uri}
import org.http4s.client.Client
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.typelevel.log4cats.Logger

import scala.annotation.unused

trait HttpClient {
  val baseUrl: Uri

  def get[F[_]: Async, A](uri: Uri)(implicit client: Client[F], decoder: Decoder[A], logger: Logger[F]): F[Either[StockClientError, A]] = {
    val uriForLog = uri.withQueryParam("apikey", "####").renderString
    client.get[Either[StockClientError, A]](uri) {
      case Status.Successful(r) =>
//        logger.info(s"Got OK response for request: $uriForLog") >>
        val zz: F[Either[StockClientParseError.type , A]] = r.attemptAs[A].leftMap(_ => StockClientParseError).value
          for {
            x <- zz
          } yield x


//      case r if r.status == Status.NotFound =>
//        logger.error(s"Got NotFound response for request with api key removed: $uriForLog, responding with $StockClientNotFoundError") >>
//          IO.pure(Left(StockClientNotFoundError))
//      case v =>
//        logger.error(s"Unhandled response with status ${v.status}, responding with $StockClientServerError") >>
//          IO.pure(Left(StockClientServerError))
    }
  }
}
