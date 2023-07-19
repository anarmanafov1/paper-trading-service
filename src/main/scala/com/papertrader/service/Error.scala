package com.papertrader.service

sealed trait Error extends Throwable

case object FailedToLoadConfError extends Error

sealed trait HttpClientError extends Error
case object HttpClientNotFoundError extends HttpClientError
case object HttpClientParseError extends HttpClientError
case class HttpClientServerError(msg: String) extends HttpClientError

sealed trait StockClientError extends HttpClientError

case class StockNotFoundError(symbol: String) extends StockClientError
case class StockClientInternalError(msg: String) extends StockClientError

sealed trait RequestValidationError extends HttpClientError
case class MissingHeaderError(msg: String) extends RequestValidationError
case class InvalidHeaderError(msg: String) extends RequestValidationError
case class MalformedBodyError(msg: String) extends RequestValidationError

sealed trait RefError extends Error
case class RefReadError(msg: String) extends RefError
case class RefWriteError(msg: String) extends RefError
