package com.papertrader.service

import cats.MonadError
import cats.effect.Ref
import cats.implicits._
import com.papertrader.service.models.{GlobalQuote, Item}
import com.papertrader.service.util.clients.AlphaVantageStockClient

import java.util.UUID

class StockService[F[_]](alphaVantageStockClient: AlphaVantageStockClient[F], basketRef: Ref[F, Map[UUID, Map[String, Int]]]) {
  def getGlobalQuote(symbol: String): F[GlobalQuote] = {
    alphaVantageStockClient.getGlobalQuote(symbol)
  }

  def addToBasket(item: Item, userId: UUID): F[Unit] = {
    basketRef.update(
      userToBasket => userToBasket.get(userId) match {
        case Some(basket) if basket.contains(item.symbol) => userToBasket ++ Map(userId -> (basket ++ Map(item.symbol -> basket(item.symbol))))
        case Some(basket) => userToBasket ++ Map(userId -> (basket ++ Map(item.symbol -> item.quantity)))
        case None => userToBasket ++ Map(userId -> Map.empty)
      }
    )
  }

  def viewBasket(userId: UUID)(implicit me: MonadError[F, Throwable]): F[Map[String, Int]] = basketRef.get.map(basket => basket.getOrElse(userId, Map.empty))
}
