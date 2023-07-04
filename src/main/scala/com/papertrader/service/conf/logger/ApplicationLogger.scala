package com.papertrader.service.conf.logger

import cats.effect.kernel.Sync
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object ApplicationLogger extends {
  implicit def getLogger[F[_]: Sync]: Logger[F] = Slf4jLogger.getLogger[F]
}
