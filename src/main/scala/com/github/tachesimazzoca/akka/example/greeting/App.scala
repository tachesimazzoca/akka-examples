package com.github.tachesimazzoca.akka.example.greeting

import akka.actor.Actor
import akka.actor.Props

class App extends Actor {
  override def preStart() {
    val greeter = context.actorOf(Props[Greeter], "greeter")
    greeter ! Greeter.Greet
  }

  def receive: Receive = {
    case Greeter.Done => context.stop(self)
  }
}
