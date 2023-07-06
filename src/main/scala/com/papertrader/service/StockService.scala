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

  // TODO: Review and simplify map logic
  def addToBasket(symbol: String, quantity: Int, userId: UUID): F[Unit] = ???
//  {
//    basketRef.update(userToBasket => userToBasket.get(userId) match {
//      case Some(basket) if basket.contains(symbol) =>
//        val currentQuantity = basket(symbol)
//        userToBasket ++ Map[UUID, Map[String, Int]](userId, basket ++ Map(symbol -> currentQuantity))
//      case Some(basket) => userToBasket ++ Map[UUID, Map[String, Int]](userId -> (basket ++ Map[String, Int](symbol, quantity)))
//      case None => userToBasket ++ Map[UUID, Map[String, Int]](userId, Map.empty)
//    })
//  }

  def viewBasket(userId: UUID)(implicit me: MonadError[F, Throwable]): F[Map[String, Int]] = basketRef.get.map(basket => basket.getOrElse(userId, Map.empty))
}
