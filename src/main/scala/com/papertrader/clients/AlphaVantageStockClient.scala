package com.papertrader.clients

import cats.effect.IO
import com.papertrader.conf.AppConf
import com.papertrader.models.GlobalQuote
import io.circe.generic.auto.exportDecoder
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.implicits.http4sLiteralsSyntax

object AlphaVantageStockClient extends HttpClient {

  val baseUrl: Uri = uri"https://www.alphavantage.co/query"
  def getGlobalQuote(symbol: String)(implicit client: Client[IO], appConf: AppConf): IO[Either[String, GlobalQuote]] = {
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


