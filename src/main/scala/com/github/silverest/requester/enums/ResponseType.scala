package com.github.silverest.requester.enums

import sttp.client3.{ResponseAs, asStringAlways}
import sttp.client3.circe.asJson

enum ResponseType:
  case Json
  case String

  def apply(): ResponseAs[Serializable, Any] =
    this match
      case Json => asJson[String]
      case String => asStringAlways