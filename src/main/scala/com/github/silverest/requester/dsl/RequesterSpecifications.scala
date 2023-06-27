package com.github.silverest.requester.dsl

import com.github.silverest.requester.*
import com.github.silverest.requester.enums.*
import com.github.silverest.requester.dsl.YamlWrapper.*
import scala.jdk.CollectionConverters.*
import monocle.Iso

object RequesterSpecifications:
  import scala.collection.JavaConverters.*

  private type JavaMap[A] = java.util.Map[String, A]
  private type JavaList[A] = java.util.List[A]

  sealed trait RequesterSpec

  case class Link (link: String) extends RequesterSpec

  case class Endpoint(name: String, path: Seq[String], isAuth: Boolean) extends RequesterSpec

  case class Login(path: Seq[String], parameters: Map[String, String], responseType: ResponseType, key: Option[String]) extends RequesterSpec

  case class Action(endpointName: String, bodyType: BodyType, body: Option[String | Map[String, String]]) extends RequesterSpec

  case class Model(name: String, parameters: Map[String, Object]) extends RequesterSpec

  case class Regex(name: String, pattern: String) extends RequesterSpec

  object Link:
    given YamlDecoder[Link] with
      def decode(yamlMap: Map[String, Object]): Link =
        Link(yamlMap("link").asInstanceOf[String])

  object Endpoint:
    private[this] val endpointToTuple =
      Iso[Endpoint, (String, Seq[String], Boolean)] {
        e => (e.name, e.path, e.isAuth) }
        { (name, path, isAuth) => Endpoint(name, path, isAuth) }

    def tupled(tuple: (String, Seq[String], Boolean)): Endpoint =
      endpointToTuple.reverseGet(tuple)

    def toTuple(endpoint: Endpoint): (String, Seq[String], Boolean) =
      endpointToTuple.get(endpoint)

    given YamlDecoder[Endpoint] with
      def decode(yamlMap: Map[String, Object]): Endpoint =
        val name = yamlMap.keys.head
        val map = yamlMap(name).asInstanceOf[JavaMap[Object]].asScala.toMap
        val isAuth = map("auth").asInstanceOf[Boolean]
        val path = map("path").asInstanceOf[String].split("/").filter(!_.isEmpty).toSeq
        Endpoint(name, path, isAuth)
    
  object Login:
    private def endpointFromString(loginEndpoint: String): Seq[String] =
      if !loginEndpoint.trim.contains(" ") then 
            loginEndpoint.split("/").filter(!_.isEmpty).toSeq
      else Seq.empty

    given YamlDecoder[Login] with
      def decode(yamlMap: Map[String, Object]): Login =
        val endpoint = endpointFromString(yamlMap("endpoint").asInstanceOf[String])
        val parameter = yamlMap("parameters").asInstanceOf[JavaMap[String]].asScala.toMap
        val responseType = 
          ResponseType.fromString(yamlMap("responseType").asInstanceOf[String]) match
            case Right(rt) => rt
            case Left(error) => throw new Exception(error)
        val key = yamlMap.get("key").asInstanceOf[Option[String]]
        Login(endpoint, parameter, responseType, key)

  object Action:
    given YamlDecoder[Action] with
      def decode(yamlMap: Map[String, Object]): Action =
        val actionName = yamlMap.keys.head
        val valsMap = yamlMap(actionName).asInstanceOf[JavaMap[Object]].asScala.toMap
        val endpointName = valsMap("endpoint").asInstanceOf[String]
        val bodyType =
          BodyType.fromString(valsMap("bodyType").asInstanceOf[String]) match
            case Right(bt) => bt
            case Left(error) => throw new Exception(error)
        val body =
          valsMap.get("body").asInstanceOf[Option[String | JavaMap[String]]] match
            case Some(str: String) => Some(str)
            case Some(map: JavaMap[String]) => Some(map.asScala.toMap)
            case None => None
        Action(endpointName, bodyType, body)

  object Model:
    given YamlDecoder[Model] with
      def decode(yamlMap: Map[String, Object]): Model =
        val name = yamlMap.keys.head
        val parameters = yamlMap(name).asInstanceOf[JavaMap[Object]].asScala.toMap
        Model(name, parameters)

  object Regex:
    given YamlDecoder[Regex] with
      def decode(yamlMap: Map[String, Object]): Regex =
        val name = yamlMap.keys.head
        val pattern = yamlMap(name).asInstanceOf[String]
        Regex(name, pattern)

  object RequestSpec:
    def fromYaml(yaml: String): List[RequesterSpec] =
      parseYaml(yaml)
        .map { case (key, value) =>
          key match
            case "link" => convertToT[Link]("link" -> value)
            case "login" => convertToT[Login](value)
            case "endpoints" => convertToListT[Endpoint](value)
            case "actions" => convertToListT[Action](value)
            case "models" => convertToListT[Model](value)
            case "regex" => convertToListT[Regex](value)
            case _ => throw new Exception(s"Invalid key: $key")
        }
        .flatMap(_.fold(e => throw e,
          r =>
            r match
              case xs: List[_ <: RequesterSpec] => xs
              case x: (_ with RequesterSpec) => List(x)
        ))
        .toList
