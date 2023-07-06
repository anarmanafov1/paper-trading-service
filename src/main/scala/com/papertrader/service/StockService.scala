package com.papertrader.service

import cats.effect.{Async, Ref}
import cats.implicits.toFunctorOps
import com.papertrader.service.conf.ApplicationConfig
import com.papertrader.service.conf.clients.AlphaVantageStockClient
import com.papertrader.service.models.GlobalQuote
import org.http4s.client.Client
import org.typelevel.log4cats.Logger
import java.util.UUID

class StockService[F[+_]: Async](basketRef: Ref[F, Map[UUID, Map[String, Int]]]) {
  def getGlobalQuote(symbol: String)(implicit client: Client[F], appConf: ApplicationConfig, logger: Logger[F]): F[Either[StockClientError, GlobalQuote]] = {
    AlphaVantageStockClient.getGlobalQuote(symbol)
  }

  // TODO: Review and simplify map logic
  def addToBasket(symbol: String, quantity: Int, userId: UUID): F[Unit] = {
    basketRef.update(userToBasket => userToBasket.get(userId) match {
      case Some(basket) if basket.contains(symbol) =>
        val currentQuantity = basket(symbol)
        userToBasket ++ Map[UUID, Map[String, Int]](userId, basket ++ Map(symbol -> currentQuantity))
      case Some(basket) => userToBasket ++ Map[UUID, Map[String, Int]](userId -> (basket ++ Map[String, Int](symbol, quantity)))
      case None => userToBasket ++ Map[UUID, Map[String, Int]](userId, Map.empty)
    })
  }

  def viewBasket(userId: UUID): F[Map[String, Int]] = basketRef.get.map(basket => basket.getOrElse(userId, Map.empty))
}
