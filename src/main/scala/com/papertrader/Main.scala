package com.papertrader

import cats.effect.{ExitCode, IO, IOApp}
import com.papertrader.service.Bootstrap

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    Bootstrap.bootstrap[IO].useForever.as(ExitCode.Success)
}
