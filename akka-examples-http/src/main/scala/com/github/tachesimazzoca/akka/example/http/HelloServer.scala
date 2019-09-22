package com.github.tachesimazzoca.akka.example.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._

import scala.io.StdIn

object HelloServer {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("routes")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val routes =
      path("ping") {
        complete("OK")
      } ~ path("hello") {
        complete(new java.util.Date().toString)
      } ~ pathEndOrSingleSlash {
        complete("Hello, this is an example HTTP server with akka-http.")
      }

    val bindingFuture = Http().bindAndHandle(routes, "localhost", 9000)

    StdIn.readLine()
    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }
}
