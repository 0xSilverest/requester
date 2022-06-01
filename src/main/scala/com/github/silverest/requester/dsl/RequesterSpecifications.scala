package com.github.silverest.requester.dsl

import com.github.silverest.requester.*
import com.github.silverest.requester.enums.*
import monocle.Iso
import io.circe.*

sealed trait RequesterSpec

case class Link (link: String) extends RequesterSpec

sealed abstract case class LoginEndpoint private (loginEndpoint: Seq[String]) extends RequesterSpec

case class Endpoint(name: String, path: Seq[String], isAuth: Boolean) extends RequesterSpec

case class Login(parameter: Map[String, String], responseType: ResponseType, key: Option[String]) extends RequesterSpec

case class Action(endpointName: String, bodyType: BodyType, body: Option[String]) extends RequesterSpec

case class Model(name: String, parameters: Map[String, String]) extends RequesterSpec

case class Regex(name: String, pattern: String) extends RequesterSpec

object LoginEndpoint:
  def empty = new LoginEndpoint(Seq.empty) {}

  def fromString(loginEndpoint: String): LoginEndpoint =
    if !loginEndpoint.trim.contains(" ") then 
        new LoginEndpoint(
          loginEndpoint.split("/").filter(!_.isEmpty).toSeq
        ) {}
    else LoginEndpoint.empty

  given loginEndpointDecoder: Decoder[LoginEndpoint] =
    Decoder.forProduct1("loginEndpoint")(LoginEndpoint.fromString)

  given loginEndpointEncoder: Encoder[LoginEndpoint] =
    Encoder.encodeString.contramap[LoginEndpoint](_.loginEndpoint.mkString("/"))

object Endpoint:
  private[this] val endpointToTuple =
    Iso[Endpoint, (String, Seq[String], Boolean)] {
      e => (e.name, e.path, e.isAuth) }
      { (name, path, isAuth) => Endpoint(name, path, isAuth) }

  def tupled(tuple: (String, Seq[String], Boolean)): Endpoint =
    endpointToTuple.reverseGet(tuple)

  def toTuple(endpoint: Endpoint): (String, Seq[String], Boolean) =
    endpointToTuple.get(endpoint)
