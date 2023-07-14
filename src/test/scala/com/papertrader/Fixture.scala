package com.papertrader

import cats.MonadError
import cats.effect.Ref
import com.papertrader.service.conf.ApplicationConfig
import com.papertrader.service.models.{Decoders, GlobalQuote}
import org.http4s.client.Client
import org.scalatestplus.mockito.MockitoSugar.mock
import org.typelevel.log4cats.Logger
import java.util.UUID
import com.papertrader.service.Error
import org.http4s.EntityDecoder

trait Fixture {
  implicit val globalQuoteentityDecoder: EntityDecoder[Either[Error, *], GlobalQuote] = mock[EntityDecoder[Either[Error, *], GlobalQuote]]
  implicit val me: MonadError[Either[Error, *], Throwable] = mock[MonadError[Either[Error, *], Throwable]]
  implicit val mockClient: Client[Either[Error, *]] = mock[Client[Either[Error, *]]]
  implicit val mockConf: ApplicationConfig = mock[ApplicationConfig]
  implicit val logger: Logger[Either[Error, *]] = mock[Logger[Either[Error, *]]]
  implicit val decoders: Decoders[Either[Error, *]] = mock[Decoders[Either[Error, *]]]
  implicit val fakeBasketRef: Ref[Either[Error, *], Map[UUID, Map[String, Int]]] = mock[Ref[Either[Error, *], Map[UUID, Map[String, Int]]]]

  val globalQuote: GlobalQuote = GlobalQuote(
    symbol = "IBM",
    price = 12.42,
    low = 11.12,
    high = 15.42
  )
}
