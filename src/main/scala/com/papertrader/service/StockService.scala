package com.papertrader.service

import cats.effect.IO
import com.papertrader.service.conf.ApplicationConfig
import com.papertrader.service.conf.clients.AlphaVantageStockClient
import com.papertrader.service.models.GlobalQuote
import org.http4s.client.Client
import org.typelevel.log4cats.Logger

object StockService {
  def getGlobalQuote(symbol: String)(implicit client: Client[IO], appConf: ApplicationConfig, logger: Logger[IO]): IO[Either[StockClientError, GlobalQuote]] = {
    AlphaVantageStockClient.getGlobalQuote(symbol)
  }
}
