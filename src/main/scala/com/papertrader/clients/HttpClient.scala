package com.papertrader.clients

import cats.effect.IO
import org.http4s.{QueryParam, Status, Uri}
import org.http4s.client.Client
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.ember.core.Parser.Request

trait HttpClient {
  val uri: Uri

  def get[A](queryParams: List[QueryParam[A]])(implicit client: Client[IO]): IO[Either[String, A]] =
    client.get[Either[String, A]](uri) {
      case Status.Successful(r) => r.attemptAs[A].leftMap(_.message).value
      case r => r.as[String]
        .map(b => Left(s"Request failed with status ${r.status.code} and body $b"))
    }

  def foo(implicit client: Client[IO]) = {

    val request = Request(hea)
    client.run()
  }
}
