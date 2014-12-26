import NativePackagerKeys._

packageArchetype.akka_application

name := "akka-examples-kernel"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.4"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.8",
  "com.typesafe.akka" %% "akka-kernel" % "2.3.8",
  "ch.qos.logback" % "logback-classic" % "1.0.7",

  "com.typesafe.akka" %% "akka-testkit" % "2.3.8" % "test",
  "org.scalatest" % "scalatest_2.10" % "1.9.2" % "test"
)

mainClass in Compile := Some("com.github.tachesimazzoca.akka.example.kernel.MainKernel")
