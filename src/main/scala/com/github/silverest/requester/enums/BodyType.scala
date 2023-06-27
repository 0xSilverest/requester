package com.github.silverest.requester.enums

enum BodyType:
  case JSON, XML, HTML, Binary
  case CustomBody(t: String, bodyType: BodyType)

object BodyType:
  def fromString(str: String): Either[String, BodyType] =
    if str.contains("@") then
      val parts = str.split("@")
      if parts.length != 2 then
        Left(s"Invalid body length: $str")
      else
        val body = parts(1)
        val t = parts(0)
        t.toLowerCase() match
          case "json" => Right(CustomBody(body, JSON))
          case "xml" => Right(CustomBody(body, XML))
          case "html" => Right(CustomBody(body, HTML))
          case "binary" => Right(CustomBody(body, Binary))
          case _ => Left(s"Invalid body type: $str")
    else
      str.toLowerCase() match
        case "json" => Right(JSON)
        case "xml" => Right(XML)
        case "html" => Right(HTML)
        case "binary" => Right(Binary)
        case _ => Left(s"Invalid body type: $str")
