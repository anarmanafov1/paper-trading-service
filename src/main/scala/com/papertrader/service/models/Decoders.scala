package com.papertrader.service.models

import io.circe.{Decoder, HCursor}

object Decoders {
  implicit val decodeGlobalQuote: Decoder[GlobalQuote] =
    new Decoder[GlobalQuote] {
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
