package com.papertrader.service.conf.clients

import cats.effect.IO
import com.papertrader.service.{StockClientError, StockClientNotFoundError, StockClientParseError, StockClientServerError}
import io.circe.Decoder
import org.http4s.{Status, Uri}
import org.http4s.client.Client
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder

trait HttpClient {
  val baseUrl: Uri

  // TODO: Add logging and use EitherT
  def get[A](uri: Uri)(implicit client: Client[IO], decoder: Decoder[A]): IO[Either[StockClientError, A]] =
    client.get[Either[StockClientError, A]](uri) {
      case Status.Successful(r) => r.attemptAs[A].leftMap(_ => StockClientParseError).value
      case r if r.status == Status.NotFound => IO.pure(Left(StockClientNotFoundError))
      case _ => IO.pure(Left(StockClientServerError))
    }
}
