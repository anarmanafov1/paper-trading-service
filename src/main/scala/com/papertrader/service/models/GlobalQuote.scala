package com.papertrader.service.models

case class GlobalQuote(
    symbol: String,
    price: BigDecimal,
    low: BigDecimal,
    high: BigDecimal
)
