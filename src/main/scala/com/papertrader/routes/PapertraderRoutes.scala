package com.papertrader.routes

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object PapertraderRoutes {

  def routes[F[_]: Sync]: HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "hello" / name =>
        for {
          resp <- Ok(name)
        } yield resp
    }
  }
}