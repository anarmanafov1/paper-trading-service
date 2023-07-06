package com.papertrader.service

import cats.MonadError
import cats.effect.Ref
import cats.implicits._
import com.papertrader.service.conf.clients.AlphaVantageStockClient
import com.papertrader.service.models.GlobalQuote

import java.util.UUID

class StockService[F[_]](alphaVantageStockClient: AlphaVantageStockClient[F], basketRef: Ref[F, Map[UUID, Map[String, Int]]]) {
  def getGlobalQuote(symbol: String): F[GlobalQuote] = {
    alphaVantageStockClient.getGlobalQuote(symbol)
  }

  def addToBasket(symbol: String, quantity: Int, userId: UUID): F[Unit] = {
    basketRef.update(
      userToBasket => userToBasket.get(userId) match {
        case Some(basket) if basket.contains(symbol) => userToBasket ++ Map(userId -> (basket ++ Map(symbol -> basket(symbol))))
        case Some(basket) => userToBasket ++ Map(userId -> (basket ++ Map(symbol -> quantity)))
        case None => userToBasket ++ Map(userId -> Map.empty)
      }
    )
  }

  def viewBasket(userId: UUID)(implicit me: MonadError[F, Throwable]): F[Map[String, Int]] = basketRef.get.map(basket => basket.getOrElse(userId, Map.empty))
}
