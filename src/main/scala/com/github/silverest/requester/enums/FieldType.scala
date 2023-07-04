package com.github.silverest.requester.enums

enum FieldType:
  case Double, Int, Boolean, List, Map, String
  case StringWithRegex(regexName: String)
  case Enum(values: List[String])
  case DoubleInRange(min: Double, max: Double)
  case IntInRange(min: Int, max: Int)
  case StringOfLength(min: Int, max: Int)

object FieldType:
  private type GensType = (Seq[String] | (Int, Int) | (Double, Double) | String)

  def fromString(fieldType: String): Either[String, FieldType] =
    def parseGen(generator: String): Either[String, GensType] =
      generator match
        case s"regex:$regexName" => Right(regexName)
        case s"range:$min:$max" => Right(min.toDouble, max.toDouble)
        case s"max:$max" => Right(0.0, max.toDouble)
        case s"min:$min" => Right(min.toDouble, scala.Double.MaxValue)
        case _ => Left(s"Unknown generator: $generator")

    def parseFieldType(fieldType: String): Either[String, FieldType] =
      fieldType.toLowerCase match
        case "double" => Right(Double)
        case "int" => Right(Int)
        case "boolean" => Right(Boolean)
        case "list" => Right(List)
        case "map" => Right(Map)
        case "string" => Right(String)
        case _ => Left(s"Unknown field type: $fieldType")

    if fieldType.contains("@") then
      val typeAndGen = fieldType.split("@")
      if typeAndGen.length != 2 then Left(s"Invalid generator: $typeAndGen")
      else
        (parseFieldType(typeAndGen(0)), parseGen(typeAndGen(1))) match
          case (Right(FieldType.String), Right(regex: String)) => Right(StringWithRegex(regex))
          case (Right(FieldType.String), Right((min: Double, max: Double))) => Right(StringOfLength(min.toInt, max.toInt))
          case (Right(FieldType.Double), Right((min: Double, max: Double))) => Right(DoubleInRange(min, max))
          case (Right(FieldType.Int), Right((min: Int, max: Int))) => Right(IntInRange(min, max))
          case (Left(error), _) => Left(error)
          case (_, Left(error)) => Left(error)
          case _ => Left(s"Invalid generator: $typeAndGen")
    else if fieldType.contains("|") then
      val values = fieldType.split("\\|").toList.map(_.replaceAll(" ", ""))
      Right(Enum(values))
    else
      parseFieldType(fieldType)

