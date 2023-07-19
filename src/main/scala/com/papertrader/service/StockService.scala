package com.papertrader.service

import cats.MonadError
import cats.effect.Ref
import cats.implicits._
import com.papertrader.service.conf.ApplicationConfig
import com.papertrader.service.models.{GlobalQuote, Item}
import com.papertrader.service.util.clients.StockClient
import org.http4s.circe.JsonDecoder
import org.http4s.client.Client
import org.typelevel.log4cats.Logger
import java.util.UUID

object StockService {
  def getGlobalQuote[F[_]](
      symbol: String
  )(implicit
    client: Client[F],
    stockClient: StockClient[F],
    appConf: ApplicationConfig,
    logger: Logger[F],
    me: MonadError[F, Throwable],
    jsonDecoder: JsonDecoder[F]
  ): F[GlobalQuote] = stockClient.getGlobalQuote(symbol)

  def addToBasket[F[_]](item: Item, userId: UUID)(implicit
      basketRef: Ref[F, Map[UUID, Map[String, Int]]]
  ): F[Unit] =
    basketRef.update(addToBasketPure(item, userId, _))

  def viewBasket[F[_]](userId: UUID)(implicit
      me: MonadError[F, Throwable],
      basketRef: Ref[F, Map[UUID, Map[String, Int]]]
  ): F[Map[String, Int]] =
    basketRef.get.map(viewBasketPure(userId, _))

  def addToBasketPure(
      item: Item,
      userId: UUID,
      globalBasket: Map[UUID, Map[String, Int]]
  ): Map[UUID, Map[String, Int]] =
    globalBasket.get(userId) match {
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

  def viewBasketPure(
      userId: UUID,
      basket: Map[UUID, Map[String, Int]]
  ): Map[String, Int] =
    basket.getOrElse(userId, Map.empty)
}
