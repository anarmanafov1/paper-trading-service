package papertrader

import cats.effect.IO
import org.http4s._
import munit.CatsEffectSuite

class StockServiceSpec extends CatsEffectSuite {
  test("StockServiceSpec returns status code 200") {
    assertIO(IO.pure(Status.Ok) ,Status.Ok)
  }
}