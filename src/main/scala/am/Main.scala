package am

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {
  val run = PapertraderServer.run[IO]
}
