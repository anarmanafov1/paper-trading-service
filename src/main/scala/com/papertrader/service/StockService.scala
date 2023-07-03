package com.papertrader.service

import cats.effect.IO
import com.papertrader.service.conf.ApplicationConfig
import com.papertrader.service.conf.clients.AlphaVantageStockClient
import com.papertrader.service.models.GlobalQuote
import org.http4s.client.Client

object StockService {
  def getGlobalQuote(symbol: String)(implicit client: Client[IO], appConf: ApplicationConfig): IO[Either[String, GlobalQuote]] = {
    AlphaVantageStockClient.getGlobalQuote(symbol)
  }
}
