package com.papertrader.service

sealed trait Error extends Throwable
sealed trait StockClientError extends Error
case object StockClientNotFoundError extends StockClientError
case object StockClientParseError extends StockClientError
case object StockClientServerError extends StockClientError
