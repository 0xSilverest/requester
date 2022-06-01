package requester

import org.scalatest.*
import flatspec.AnyFlatSpec
import matchers.should.Matchers

import cats.data.NonEmptyList

import com.github.silverest.requester.dsl.*

class ParserSpec extends AnyFlatSpec with Matchers {
  import cats.parse.Parser.Error

  "A link" should """parse "link: localhost:8080" """ in {
    val string = "link: localhost:8080"

    RequesterYaml.link(string) should be (Right[io.circe.Error, Link](
      Link("localhost:8080")))
  }
  
  it should """parse "link: https://github.com" """ in {
    val string = "link: https://github.com"

    RequesterYaml.link(string) should be (Right[io.circe.Error, Link](
      Link("https://github.com")))
  }

  it should """parse "link: 192.168.1.1" """ in {
    val string = "link: 192.168.1.1"

    RequesterYaml.link(string) should be (Right[io.circe.Error, Link](
      Link("192.168.1.1")))
  }

  "A loginEndpoint" should """parse "loginEndpoint: /api/auth/login" """ in {
    val string =
      "loginEndpoint: /api/auth/login"

    RequesterYaml.loginEndpoint(string) should be (Right[io.circe.Error, LoginEndpoint](
      LoginEndpoint.fromString("/api/auth/login")))
  }

  it should """parse "loginEndpoint: api/auth/login" """ in {
    val string =
      "loginEndpoint: api/auth/login"

    RequesterYaml.loginEndpoint(string) should be (Right[io.circe.Error, LoginEndpoint](
      LoginEndpoint.fromString("/api/auth/login")))
  }
}
