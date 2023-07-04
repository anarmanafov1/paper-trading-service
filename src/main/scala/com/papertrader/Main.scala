package com.papertrader

import cats.effect.{IO, IOApp}
import com.papertrader.service.Bootstrap

object Main extends IOApp.Simple {
  val run: IO[Unit] = Bootstrap.all
}
