package com.papertrader.service

import cats.MonadError
import cats.effect.Ref
import cats.implicits._
import com.papertrader.service.models.{GlobalQuote, Item}
import com.papertrader.service.util.clients.AlphaVantageStockClient
import java.util.UUID

class StockService[F[_]](alphaVantageStockClient: AlphaVantageStockClient[F], basketRef: Ref[F, Map[UUID, Map[String, Int]]]) {
  def getGlobalQuote(symbol: String): F[GlobalQuote] = alphaVantageStockClient.getGlobalQuote(symbol)

  def addToBasket(item: Item, userId: UUID): F[Unit] =
    basketRef.update(
      globalBasket => globalBasket.get(userId) match {
        case Some(userBasket) if userBasket.contains(item.symbol) =>
          val newQuantity = (userBasket(item.symbol) + item.quantity)
          val updatedUserBasket = userBasket + (item.symbol -> newQuantity)
          globalBasket + (userId -> updatedUserBasket)
        case Some(userBasket) =>
          val updatedUserBasket = userBasket + (item.symbol -> item.quantity)
          globalBasket + (userId -> updatedUserBasket)
        case None =>
          val newUserBasket = Map(item.symbol -> item.quantity)
          globalBasket + (userId -> newUserBasket)
      }
    )

  def viewBasket(userId: UUID)(implicit me: MonadError[F, Throwable]): F[Map[String, Int]] = basketRef.get.map(basket => basket.getOrElse(userId, Map.empty))
}
