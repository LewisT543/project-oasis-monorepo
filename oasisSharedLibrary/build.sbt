ThisBuild / version := "1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.3"

lazy val root = (project in file("."))
  .settings(
    name := "oasisSharedLibrary"
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % Test