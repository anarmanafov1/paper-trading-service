package com.papertrader.service.conf

import cats.MonadError
import pureconfig.ConfigSource
import pureconfig.generic.auto._

case class ApplicationConfig(
  AlphaVantageApiKey: String
)

object ApplicationConfig {
  def load[F[_]]()(implicit monadError: MonadError[F, Throwable]): F[ApplicationConfig] =
    monadError
      .fromEither(
        ConfigSource.default.load[ApplicationConfig]
          .left
          .map(e => new Throwable(e.prettyPrint())
          )
      )
}