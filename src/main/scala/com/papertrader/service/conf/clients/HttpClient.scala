package com.papertrader.service.conf.clients

import cats.effect.IO
import io.circe.Decoder
import org.http4s.{Status, Uri}
import org.http4s.client.Client
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder

trait HttpClient {
  val baseUrl: Uri
  def get[A](uri: Uri)(implicit client: Client[IO], decoder: Decoder[A]): IO[Either[String, A]] =
    client.get[Either[String, A]](uri) {
      case Status.Successful(r) => r.attemptAs[A].leftMap(_.message).value
      case r => r.as[String]
        .map(b => Left(s"Request failed with status ${r.status.code} and body $b"))
    }
}
