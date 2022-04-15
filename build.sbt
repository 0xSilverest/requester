ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.1"

val zioV = "1.0.13"
val sttpV = "3.5.1"
val circeV = "0.14.1"

lazy val root = (project in file("."))
  .settings(
    name := "requester",
    version := "0.1.0",

    scalacOptions ++= Seq(
      "-language:postfixOps"
    ),

    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioV,

      "com.softwaremill.sttp.client3" %% "core" % sttpV,
      "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % sttpV,
      "com.softwaremill.sttp.client3" %% "circe" % sttpV,

      "io.circe" %% "circe-core" % circeV,
      "io.circe" %% "circe-generic" % circeV,
      "io.circe" %% "circe-parser" % circeV

    )
  )

enablePlugins(JavaAppPackaging)

