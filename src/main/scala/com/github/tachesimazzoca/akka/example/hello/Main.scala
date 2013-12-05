package com.github.tachesimazzoca.akka.example.hello

import akka.actor.Actor
import akka.actor.Props

class Main extends Actor {
  val greeter = context.actorOf(Props[Greeter], "greeter")
  greeter ! Greeter.Request

  def receive: Receive = {
    case Greeter.Response(msg) =>
      println(msg)
      context.stop(self)
  }
}
