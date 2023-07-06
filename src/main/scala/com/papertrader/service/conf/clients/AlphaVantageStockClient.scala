package com.papertrader.service.conf.clients

import cats.MonadError
import cats.effect.kernel.Concurrent
import com.papertrader.service.conf.ApplicationConfig
import com.papertrader.service.models.{Decoders, GlobalQuote}
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.implicits.http4sLiteralsSyntax
import org.typelevel.log4cats.Logger

class AlphaVantageStockClient[F[_]]()(implicit client: Client[F], appConf: ApplicationConfig, logger: Logger[F], me: MonadError[F, Throwable], cz: Concurrent[F])
  extends HttpClient
    with Decoders[F] {

  implicit val c: Concurrent[F] = cz

  val baseUrl: Uri = uri"https://www.alphavantage.co/query"
  def getGlobalQuote(symbol: String): F[GlobalQuote] = {
    val request = baseUrl
      .withPath(path"query")
      .withQueryParams(
        Map(
          "function" -> "GLOBAL_QUOTE",
          "symbol" -> symbol,
          "apikey" -> appConf.AlphaVantageApiKey
        )
      )
    get[F, GlobalQuote](request)(client, decodeGlobalQuoteF, logger, me)
  }
}