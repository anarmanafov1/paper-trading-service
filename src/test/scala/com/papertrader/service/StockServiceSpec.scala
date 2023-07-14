package com.papertrader.service

import com.papertrader.Fixture
import com.papertrader.service.util.clients._
import munit.CatsEffectSuite
import com.papertrader.service.Error

class StockServiceSpec extends CatsEffectSuite with Fixture {

  implicit val alphaVantageStockClient: AlphaVantageStockClient[Either[Error, *]] = new MockAlphaVantageClient {}

  test("service.getGlobalQuote returns quote when stock client returns quote successfully") {
    val r = StockService.getGlobalQuote(MockAlphaVantageClient.stockClientSuccessCase)
    assertEquals(r, Right(globalQuote))
  }

  test("service.getGlobalQuote returns HttpClientParseError when stock client returns HttpClientParseError") {
    val r = StockService.getGlobalQuote(MockAlphaVantageClient.stockClientHttpClientParseErrorCase)
    assertEquals(r, Left(HttpClientParseError))
  }

  test("service.getGlobalQuote returns HttpClientServerError when stock client returns HttpClientServerError") {
    val r = StockService.getGlobalQuote(MockAlphaVantageClient.stockClientHttpClientServerErrorCase)
    assertEquals(r, Left(MockAlphaVantageClient.stockClientHttpClientServerError))
  }

  test("service.getGlobalQuote returns HttpClientNotFoundError when stock client returns HttpClientNotFoundError") {
    val r = StockService.getGlobalQuote(MockAlphaVantageClient.stockClientHttpClientNotFoundErrorCase)
    assertEquals(r, Left(HttpClientNotFoundError))
  }
}
