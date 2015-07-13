name := "scalarelational-benchmarks"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.0.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.scalarelational" %% "scalarelational-h2" % "1.0.1-SNAPSHOT",
  "org.scalarelational" %% "scalarelational-mapper" % "1.0.1-SNAPSHOT"
)

enablePlugins(JmhPlugin)
