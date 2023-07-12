package com.papertrader.service

import cats.MonadError
import cats.effect.Ref
import com.papertrader.service.conf.ApplicationConfig
import com.papertrader.service.models.{Decoders, GlobalQuote}
import com.papertrader.service.util.clients.AlphaVantageStockClient
import munit.CatsEffectSuite
import org.http4s.client.Client
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import org.typelevel.log4cats.Logger
import java.util.UUID

class StockServiceSpec extends CatsEffectSuite {

  implicit val mockClient: Client[Option] = mock[Client[Option]]
  implicit val mockConf: ApplicationConfig = mock[ApplicationConfig]
  implicit val logger: Logger[Option] = mock[Logger[Option]]
  implicit val decoders: Decoders[Option] = mock[Decoders[Option]]
  implicit val me: MonadError[Option, Throwable] = mock[MonadError[Option, Throwable]]
  val quote: GlobalQuote = GlobalQuote(
    symbol = "IBM",
    price = 12.42,
    low = 11.12,
    high = 15.42
  )

  val fakeAlphaVantageStockClient: AlphaVantageStockClient[Option] = mock[AlphaVantageStockClient[Option]]
  val fakeBasketRef: Ref[Option, Map[UUID, Map[String, Int]]] = mock[Ref[Option, Map[UUID, Map[String, Int]]]]
  val service = new StockService[Option](fakeAlphaVantageStockClient, fakeBasketRef)

  test("StockServiceSpec returns status code 200") {

    when(fakeAlphaVantageStockClient.getGlobalQuote(anyString())).thenReturn(Some(quote))

    val r = service.getGlobalQuote("ibm")
    assertEquals(r, Some(quote))
  }
}

















