package com.papertrader.service.util.controllers

import cats.MonadError
import cats.data.NonEmptyList
import org.http4s.{Header, Request}
import org.typelevel.ci.CIString
import com.papertrader.service.{InvalidHeaderError, MissingHeaderError}
import cats.implicits._
import com.papertrader.service.models.Item
import cats.implicits.toFlatMapOps
import com.papertrader.service._
import org.http4s.circe._
import io.circe.generic.auto._
import java.util.UUID
import scala.util.Try
object RequestValidation {
  def validateUserIdHeader[F[_]](r: Request[F])(implicit me: MonadError[F, Throwable]): F[UUID] = for {
    userHeader: Option[NonEmptyList[Header.Raw]] <- me.pure(r.headers.get(CIString("user-id")))
    userIdRaw <- me.fromEither(userHeader.map(_.head).toRight(MissingHeaderError("header user-id not found")))
    userId <- me.fromTry(Try(UUID.fromString(userIdRaw.value))).adaptError(e => InvalidHeaderError(s"User Id header provided not valid UUID - msg: ${e.getMessage}"))
  } yield userId

  def validateBodyAsItem[F[_]](r: Request[F])(implicit me: MonadError[F, Throwable], jsonDecoder: JsonDecoder[F]): F[Item] = for {
    json <- r.asJson
    item <- me.fromEither(json.as[Item].left.map(e => MalformedBodyError(s"Body malformed, msg: ${e.getMessage()}")))
  } yield item

}
