package com.github.silverest.requester.parser

import org.yaml.snakeyaml.*
import scala.jdk.CollectionConverters.*

object YamlWrapper:

  private type JavaMap[A] = java.util.Map[String, A]
  private type JavaLinkedHashMap[A] = java.util.LinkedHashMap[String, A]
  private type JavaList[A] = java.util.List[A]
  
  trait YamlDecoder[T]:
    def decode(yamlMap: Map[String, Object]): T

  given YamlDecoder[List[_]] with
    def decode(yamlMap: Map[String, Object]): List[_] =
      yamlMap("list").asInstanceOf[java.util.List[Object]].asScala.toList

  def parseYaml(yaml: String): Map[String, Object] =
    val yamlParser = Yaml()
    yamlParser.load(yaml).asInstanceOf[java.util.Map[String, Object]].asScala.toMap

  def parseYamlList(yaml: String): List[Object] =
    val yamlParser = Yaml()
    yamlParser.load(yaml).asInstanceOf[java.util.List[Object]].asScala.toList

  def convertYamlToListOfType[T](yamlList: List[Object])(using decoderT: YamlDecoder[T]): List[T] =
    yamlList.map(_.asInstanceOf[java.util.Map[String, Object]].asScala.toMap).map(decoderT.decode)


  def convertToListT[T](yamlObject: Object)(using decoderT: YamlDecoder[T]): Either[Throwable, List[T]] =
    yamlObject match
      case yamlList: JavaList[_] => Right(convertYamlToListOfType[T](fromJavaCollection(yamlList).asInstanceOf[List[Object]]))
      case sList: List[_] => Right(convertYamlToListOfType[T](sList.asInstanceOf[List[Object]]))
      case yamlMap: JavaLinkedHashMap[_] => Right(
        yamlMap.asScala.toMap.map((k, o) => decoderT.decode(Map(k -> o.asInstanceOf[Object]))).toList
      )
      case _ => Left(new Exception("Yaml object is not a list"))

  def convertToT[T](yamlObject: Object)(using decoderT: YamlDecoder[T]): Either[Throwable, T] =
    yamlObject match
      case yamlMap: JavaMap[_] => Right(decoderT.decode(fromJavaCollection(yamlMap).asInstanceOf[Map[String, Object]]))
      case sMap: Map[_, _] => Right(decoderT.decode(sMap.asInstanceOf[Map[String, Object]]))
      case tuple: (String, Object) => Right(decoderT.decode(Map(tuple)))
      case _ => Left(new Exception("Yaml object is not a map"))


  private def fromJavaCollection[A](javaCollection: JavaList[A]): List[A] =
    javaCollection.asScala.toList.asInstanceOf[List[A]]  

  private def fromJavaCollection[A](javaCollection: JavaMap[A]): Map[String, A] =
    javaCollection.asScala.toMap.asInstanceOf[Map[String, A]]
