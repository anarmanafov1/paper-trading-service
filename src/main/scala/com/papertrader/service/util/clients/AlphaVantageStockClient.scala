package com.papertrader.service.util.clients

import cats.MonadError
import com.papertrader.service.conf.ApplicationConfig
import com.papertrader.service.models.{Decoders, GlobalQuote}
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.implicits.http4sLiteralsSyntax
import org.typelevel.log4cats.Logger

class AlphaVantageStockClient[F[_]]()(implicit client: Client[F], appConf: ApplicationConfig, logger: Logger[F], me: MonadError[F, Throwable], decoders: Decoders[F]) extends HttpClient {

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
    get[F, GlobalQuote](request)(client, decoders.decodeGlobalQuoteF, logger, me)
  }
}