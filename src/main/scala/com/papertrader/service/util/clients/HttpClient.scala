package com.papertrader.service.util.clients

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
      case r if r.status == Status.Ok =>
        for {
          _ <- logger.error(s"Got OK response for request: $uriForLog")
          maybeParsed <- r.attemptAs[A].value
          parsed <- maybeParsed match {
            case Left(decodeFailure) => logger.error(s"Failed to decode resp with err: ${decodeFailure.getMessage}") *> me.raiseError(HttpClientParseError)
            case Right(v) => logger.info(s"Successfully decoded response $v") *> me.pure(v)
          }
        } yield parsed
      case r if r.status == Status.NotFound =>
        logger.error(s"Got NotFound response for request with api key removed: $uriForLog, responding with $HttpClientNotFoundError") *> me.raiseError(HttpClientNotFoundError)
      case v =>
        logger.error(s"Unhandled response with status ${v.status}, responding with $HttpClientServerError") *>
          me.raiseError(HttpClientServerError(s"Unhandled HTTP response ${v.status}"))
    }.handleErrorWith(e =>
      logger.error(s"Error running HttpClient get, responding with $HttpClientServerError") *>
        me.raiseError(HttpClientServerError(s"HTTP Client error: ${e.getMessage}"))
    )
  }
}
