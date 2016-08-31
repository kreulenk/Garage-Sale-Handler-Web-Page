name := """play-java-1"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  "org.avaje.ebeanorm" % "avaje-ebeanorm" % "4.6.2",
  javaWs
)
libraryDependencies += jdbc
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.36"
