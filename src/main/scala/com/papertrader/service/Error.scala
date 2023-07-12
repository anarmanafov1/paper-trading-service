package com.papertrader.service

sealed trait Error extends Throwable
sealed trait HttpClientError extends Error
case object HttpClientNotFoundError extends HttpClientError
case object HttpClientParseError extends HttpClientError
case object HttpClientServerError extends HttpClientError

sealed trait RequestValidationError extends HttpClientError

case class MissingHeaderError(msg: String) extends RequestValidationError
case class InvalidHeaderError(msg: String) extends RequestValidationError
case class MalformedBodyError(msg: String) extends RequestValidationError

case object FailedToLoadConfError extends Error