name := "akka-examples"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.2"

scalacOptions ++= Seq("-deprecation", "-unchecked")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.1.4",
  "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"
)