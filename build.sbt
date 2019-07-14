ThisBuild / scalaVersion := "2.13.0"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "ru.d10xa"

lazy val root = (project in file(".")).
  settings(
    name := "har-cleaner",
    assemblyJarName in assembly := "har-cleaner.jar",
    mainClass in assembly := Some("ru.d10xa.har-cleaner.Main"),
    test in assembly := {},
    scalacOptions := Seq(
      "-encoding", "UTF-8", // source files are in UTF-8
      "-deprecation", // warn about use of deprecated APIs
      "-unchecked", // warn about unchecked type parameters
      "-feature", // warn about misused language features
      "-language:higherKinds", // allow higher kinded types without `import scala.language.higherKinds`
      "-Xlint", // enable handy linter warnings
      "-Xfatal-warnings" // turn compiler warnings into errors
    )
  )

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test
libraryDependencies += "io.circe" %% "circe-parser" % "0.12.0-M3"
libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.8.0"
libraryDependencies += "io.circe" %% "circe-core" % "0.12.0-M3"
libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.11.1"
