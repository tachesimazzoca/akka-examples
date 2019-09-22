name := "akka-examples-stream"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.6"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.17"
)
