package com.github.tachesimazzoca.akka.example.greeting

import akka.actor.Actor

object Greeter {
  case object Greet
  case object Done
}

class Greeter extends Actor {
  def receive: Receive = {
    case Greeter.Greet =>
      println("Hello World!")
      sender ! Greeter.Done
  }
}
