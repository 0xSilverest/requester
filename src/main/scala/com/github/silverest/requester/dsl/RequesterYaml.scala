package com.github.silverest.requester.dsl

import cats.data.*
import sttp.client3.*

import cats.syntax.either.*
import io.circe.*
import io.circe.parser.*
import io.circe.generic.auto.*
import io.circe.yaml.parser

object RequesterYaml:
  import LoginEndpoint.*
  import Link.*

  private def parse[A <: RequesterSpec](input: String)(using a: Decoder[A]): Either[String, A] =
    yaml.parser.parse(input)
      .flatMap(_.as[A])
      .leftMap(_.toString)

  def link(input: String): Either[String, Link] =
    parse[Link](input)

  def loginEndpoint(input: String): Either[String, LoginEndpoint] =
    parse[LoginEndpoint](input)

  @main
  def test(): Unit = 
    val input = """{"loginEndpoint": " adsf  /www/shit/com"}"""
    //val result = loginEndpoint(input)
    //val input = "link: www.commit.com"
    //val result = link(input)
    println(decode[LoginEndpoint](input))
