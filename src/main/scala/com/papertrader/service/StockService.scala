package com.papertrader.service

import cats.effect.Async
import com.papertrader.service.conf.ApplicationConfig
import com.papertrader.service.conf.clients.AlphaVantageStockClient
import com.papertrader.service.models.GlobalQuote
import org.http4s.client.Client
import org.typelevel.log4cats.Logger

object StockService {
  def getGlobalQuote[F[_]: Async](symbol: String)(implicit client: Client[F], appConf: ApplicationConfig, logger: Logger[F]): F[Either[StockClientError, GlobalQuote]] = {
    AlphaVantageStockClient.getGlobalQuote(symbol)
  }
//  def addToBasket(symbol: String, quantity: Int)(basketRef: Ref[IO, Map[String, String]]) = {
//    basketRef.update(f => f + ("a" -> "b"))
//  }
//  def viewBasket(symbol: String, quantity: Int) = ???
}
