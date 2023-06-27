ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

val sttpV = "3.5.1"
val circeV = "0.14.1"
val scalaParserV = "2.1.1"
val catsEffectV = "3.3.11"
val catsV = "2.7.0"
val catsParseV = "0.3.7"
val monocleV = "3.1.0"
val log4catsV = "2.2.0"

lazy val root = (project in file("."))
  .settings(
    name := "requester",

    scalacOptions ++= Seq(
        "-language:postfixOps"
    ),

    libraryDependencies ++= Seq(
        "dev.optics" %% "monocle-core" % monocleV,

        "com.softwaremill.sttp.client3" %% "core" % sttpV,
        "com.softwaremill.sttp.client3" %% "async-http-client-backend-cats" % sttpV,
        "com.softwaremill.sttp.client3" %% "circe" % sttpV,

        "io.circe" %% "circe-core" % circeV,
        "io.circe" %% "circe-generic" % circeV,
        "io.circe" %% "circe-parser" % circeV,

        "org.yaml" % "snakeyaml" % "2.0",

        "org.scalacheck" %% "scalacheck" % "1.16.0" % "test",
        "org.scalatest" %% "scalatest" % "3.2.11" % "test"
    )
  )

enablePlugins(JavaAppPackaging)

