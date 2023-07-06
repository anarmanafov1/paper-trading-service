package com.papertrader.service.conf.clients

import cats.effect.Async
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
  def getGlobalQuote[F[+_]: Async](symbol: String)(implicit client: Client[F], appConf: ApplicationConfig, logger: Logger[F]): F[Either[StockClientError, GlobalQuote]] = {
    val request = baseUrl
      .withPath(path"query")
      .withQueryParams(
        Map(
          "function" -> "GLOBAL_QUOTE",
          "symbol" -> symbol,
          "apikey" -> appConf.AlphaVantageApiKey
        )
      )
    get[F, GlobalQuote](request)
  }
}


