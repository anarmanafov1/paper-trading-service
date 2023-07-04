package com.papertrader.service.conf.clients

import cats.effect.IO
import com.papertrader.service.StockClientError
import com.papertrader.service.conf.ApplicationConfig
import com.papertrader.service.models.GlobalQuote
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.implicits.http4sLiteralsSyntax
import com.papertrader.service.models.Decoders.decodeGlobalQuote
import org.typelevel.log4cats.Logger

object AlphaVantageStockClient extends HttpClient {

  val baseUrl: Uri = uri"https://www.alphavantage.co/query"
  def getGlobalQuote(symbol: String)(implicit client: Client[IO], appConf: ApplicationConfig, logger: Logger[IO]): IO[Either[StockClientError, GlobalQuote]] = {
    val request = baseUrl
      .withPath(path"query")
      .withQueryParams(
        Map(
          "function" -> "GLOBAL_QUOTE",
          "symbol" -> symbol,
          "apikey" -> appConf.AlphaVantageApiKey
        )
      )
    get[GlobalQuote](request)
  }
}


