name := "akka-examples"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.2"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.3",
  "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"
)
