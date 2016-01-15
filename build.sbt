name := "scalarelational-benchmarks"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "org.slf4j" % "slf4j-nop" % "1.7.13",
  "org.scalarelational" %% "scalarelational-h2" % "1.2.0-SNAPSHOT",
  "org.scalarelational" %% "scalarelational-mapper" % "1.2.0-SNAPSHOT"
)

enablePlugins(JmhPlugin)
