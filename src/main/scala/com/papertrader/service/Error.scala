package com.papertrader.service

sealed trait Error extends Throwable
sealed trait StockClientError extends Error
case object StockClientNotFoundError extends StockClientError
case object StockClientParseError extends StockClientError
case object StockClientServerError extends StockClientError

sealed trait RequestValidationError extends StockClientError

case class MissingHeaderError(msg: String) extends RequestValidationError
case class InvalidHeaderError(msg: String) extends RequestValidationError
case class MalformedBodyError(msg: String) extends RequestValidationError

case object FailedToLoadConfError extends Error