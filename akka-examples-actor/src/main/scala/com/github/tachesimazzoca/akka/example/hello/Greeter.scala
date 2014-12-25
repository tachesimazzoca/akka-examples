package com.github.tachesimazzoca.akka.example.hello

import akka.actor.Actor

object Greeter {
  case object Request
  case class Response(message: String)
}

class Greeter extends Actor {
  def receive: Receive = {
    case Greeter.Request =>
      sender ! Greeter.Response("Hello")
  }
}
