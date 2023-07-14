package com.papertrader.service.util.clients
import cats.MonadError
import com.papertrader.Fixture
import com.papertrader.service.conf.ApplicationConfig
import com.papertrader.service.models.{Decoders, GlobalQuote}
import org.http4s.client.Client
import org.typelevel.log4cats.Logger
import com.papertrader.service.{
  Error,
  HttpClientNotFoundError,
  HttpClientParseError,
  HttpClientServerError
}

trait MockAlphaVantageClient
    extends AlphaVantageStockClient[Either[Error, *]]
    with Fixture {
  override def getGlobalQuote(symbol: String)(implicit
      client: Client[Either[Error, *]],
      appConf: ApplicationConfig,
      logger: Logger[Either[Error, *]],
      me: MonadError[Either[Error, *], Throwable],
      decoders: Decoders[Either[Error, *]]
  ): Either[Error, GlobalQuote] =
    symbol match {
      case MockAlphaVantageClient.stockClientSuccessCase => Right(globalQuote)
      case MockAlphaVantageClient.stockClientHttpClientParseErrorCase =>
        Left(HttpClientParseError)
      case MockAlphaVantageClient.stockClientHttpClientNotFoundErrorCase =>
        Left(HttpClientNotFoundError)
      case MockAlphaVantageClient.stockClientHttpClientServerErrorCase =>
        Left(MockAlphaVantageClient.stockClientHttpClientServerError)
      case _ => Right(globalQuote)
    }
}

object MockAlphaVantageClient {
  val stockClientSuccessCase = "stockClientSuccessCase"
  val stockClientHttpClientParseErrorCase =
    "stockClientHttpClientParseErrorCase"
  val stockClientHttpClientNotFoundErrorCase =
    "stockClientHttpClientNotFoundErrorCase"
  val stockClientHttpClientServerErrorCase =
    "stockClientHttpClientServerErrorCase"
  val stockClientHttpClientServerError: HttpClientServerError =
    HttpClientServerError("err")
}
