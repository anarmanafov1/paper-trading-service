package com.papertrader.clients
import cats.effect.IO
import com.papertrader.models.Foo
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.{Status, Uri}
import org.http4s.client.Client
import org.http4s.implicits.http4sLiteralsSyntax
import io.circe.generic.auto._
object AlphaVantageStockClient extends HttpClient {
  val uri: Uri = uri"https://www.alphavantage.co/query"
  def getFoo()(implicit client: Client[IO]): IO[Either[String, Foo]] = get[Foo]()





  // https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=IBM&apikey=api_key
}


