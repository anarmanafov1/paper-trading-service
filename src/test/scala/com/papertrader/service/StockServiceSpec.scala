package com.papertrader.service

import com.papertrader.Fixture
import munit.CatsEffectSuite
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.when

class StockServiceSpec extends CatsEffectSuite with Fixture {

  val service = new StockService[Either[Error, *]](fakeAlphaVantageStockClient, fakeBasketRef)

  test("service.getGlobalQuote returns quote when stock client returns quote successfully") {
    when(fakeAlphaVantageStockClient.getGlobalQuote(anyString())).thenReturn(Right(quote))
    val r = service.getGlobalQuote("ibm")
    assertEquals(r, Right(quote))
  }

  test("service.getGlobalQuote returns HttpClientParseError when stock client returns HttpClientParseError") {
    when(fakeAlphaVantageStockClient.getGlobalQuote(anyString())).thenReturn(Left(HttpClientParseError))
    val r = service.getGlobalQuote("ibm")
    assertEquals(r, Left(HttpClientParseError))
  }

  test("service.getGlobalQuote returns HttpClientServerError when stock client returns HttpClientServerError") {
    when(fakeAlphaVantageStockClient.getGlobalQuote(anyString())).thenReturn(Left(HttpClientServerError))
    val r = service.getGlobalQuote("ibm")
    assertEquals(r, Left(HttpClientServerError))
  }

  test("service.getGlobalQuote returns HttpClientNotFoundError when stock client returns HttpClientNotFoundError") {
    when(fakeAlphaVantageStockClient.getGlobalQuote(anyString())).thenReturn(Left(HttpClientNotFoundError))
    val r = service.getGlobalQuote("ibm")
    assertEquals(r, Left(HttpClientNotFoundError))
  }
}

















