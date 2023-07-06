package com.papertrader.service.models

import cats.effect.kernel.Concurrent
import io.circe.{Decoder, HCursor}
import org.http4s.EntityDecoder
import org.http4s.circe.accumulatingJsonOf

case class GlobalQuote(
  symbol: String,
  price: BigDecimal,
  low: BigDecimal,
  high: BigDecimal
)

trait Decoders[F[_]] {
  implicit val c: Concurrent[F]
  implicit val decodeGlobalQuoteF: EntityDecoder[F, GlobalQuote] = accumulatingJsonOf[F, GlobalQuote](c, Decoders.decodeGlobalQuote)
}

object Decoders {
  implicit val decodeGlobalQuote: Decoder[GlobalQuote] = new Decoder[GlobalQuote] {
    final def apply(c: HCursor): Decoder.Result[GlobalQuote] = {
      val innerObject = c.downField("Global Quote")
      for {
        symbol <- innerObject.downField("01. symbol").as[String]
        price <- innerObject.downField("05. price").as[BigDecimal]
        low <- innerObject.downField("04. low").as[BigDecimal]
        high <- innerObject.downField("03. high").as[BigDecimal]
      } yield GlobalQuote(symbol, price, low, high)
    }
  }
}
