package com.papertrader.service.conf

import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderFailures
import pureconfig.generic.auto._

case class ApplicationConfig(
  AlphaVantageApiKey: String
)

object ApplicationConfig {
  def load: Either[ConfigReaderFailures, ApplicationConfig] =
    ConfigSource.default.load[ApplicationConfig]

}