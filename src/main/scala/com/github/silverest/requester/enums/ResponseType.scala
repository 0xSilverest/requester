package com.github.silverest.requester.enums

import sttp.client3.{ResponseAs, asStringAlways}
import sttp.client3.circe.asJson

enum ResponseType:
  case JSON
  case STRING

  def get: ResponseAs[Serializable, Any] =
    this match
      case JSON => asJson[String]
      case STRING => asStringAlways

object ResponseType:
  def fromString(str: String): Either[String, ResponseType] =
    str.toLowerCase() match
      case "json" => Right(JSON)
      case "string" => Right(STRING)
      case _ => Left(s"Invalid response type: $str")
