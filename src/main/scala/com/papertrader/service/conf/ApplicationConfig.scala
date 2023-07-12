package com.papertrader.service.conf

import cats.MonadError
import cats.implicits._
import com.papertrader.service.FailedToLoadConfError
import org.typelevel.log4cats.Logger
import pureconfig.ConfigSource
import pureconfig.generic.auto._

case class ApplicationConfig(
  AlphaVantageApiKey: String
)

object ApplicationConfig {
  def load[F[_]]()(implicit me: MonadError[F, Throwable], logger: Logger[F]): F[ApplicationConfig] = for {
      conf <- me
        .fromEither(ConfigSource.default.load[ApplicationConfig].left.map(failure => new Throwable(failure.prettyPrint())))
        .handleErrorWith(e => logger.error(e.getMessage) *> me.raiseError(FailedToLoadConfError))
    } yield conf
}