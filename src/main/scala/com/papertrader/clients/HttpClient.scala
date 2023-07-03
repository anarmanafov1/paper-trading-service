package com.papertrader.clients

import cats.effect.IO
import io.circe.Decoder
import org.http4s.client.Client
trait HttpClient {

  def get[A]()(implicit client: Client[IO], parser: Decoder[A]) = ???
}
