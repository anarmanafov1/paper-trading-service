package com.papertrader

import cats.effect.{IO, IOApp}
import com.papertrader.service.PapertraderServer

object Main extends IOApp.Simple {
  val run = PapertraderServer.run[IO]
}
