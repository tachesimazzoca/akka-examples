name := "akka-examples"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.4"

fork := true

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.8",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.8",
  "com.ning" % "async-http-client" % "1.7.20",
  "ch.qos.logback" % "logback-classic" % "1.0.7",
  "org.scalatest" % "scalatest_2.10" % "1.9.2" % "test",
  "junit" % "junit" % "4.10" % "test"
)
