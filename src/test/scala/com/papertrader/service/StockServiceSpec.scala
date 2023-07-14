package com.papertrader.service

import com.papertrader.Fixture
import com.papertrader.service.models.Item
import com.papertrader.service.util.clients._
import munit.CatsEffectSuite
import org.scalatest.matchers.should._

import java.util.UUID

class StockServiceSpec extends CatsEffectSuite with Fixture with Matchers {

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

  test("service.viewBasketFunc returns non empty basket for user when one exists") {
    StockService.viewBasketFunc(userId, Map(userId -> testBasket)) should equal(testBasket)
  }

  test("service.viewBasketFunc returns empty basket for user when non exists") {
    StockService.viewBasketFunc(userId2, Map(userId -> testBasket)) should equal(Map.empty)
  }

  val b: Map[UUID, Map[String, Int]] = Map(userId -> Map("nvda" -> 20))

  test("service.addToBasketFunc adds to users basket when user basket is found and already owns specified stock (top up)") {
    StockService.addToBasketFunc(Item("nvda", 5), userId, b) should equal(Map(userId -> Map("nvda" -> (25))))
  }

  test("service.addToBasketFunc adds to users basket when user basket is found and does not own specified stock") {
    StockService.addToBasketFunc(Item("ibm", 5), userId, b) should equal(Map(userId -> Map("nvda" -> (20), "ibm" -> 5)))
  }

  test("service.addToBasketFunc adds to users basket when user basket is not found") {
    StockService.addToBasketFunc(Item("ibm", 5), userId2, b) should equal(Map(userId -> Map("nvda" -> 20), userId2 -> Map("ibm" -> 5)))
  }
}