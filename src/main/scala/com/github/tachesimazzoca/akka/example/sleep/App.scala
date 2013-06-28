package com.github.tachesimazzoca.akka.example.sleep

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props

class App extends Actor with ActorLogging {
  override def preStart(): Unit = {
    val sleeper = context.actorOf(Props[Sleeper])
    sleeper ! (App.PrintRequest("Hello1 after 2sec"), 2000L)
    sleeper ! (App.PrintRequest("Hello2 after 1sec"), 1000L)
    sleeper ! "Bad message"
    sleeper ! ("Bad message after 3sec", 3000L)
    sleeper ! (App.ShutdownRequest, 5000L)
  }

  def receive = {
    case App.PrintRequest(str: String) => println(str)
    case App.ShutdownRequest => context.stop(self)
    case m @ _ => log.info("Unknown message - " + m)
  }
}

object App {
  case class PrintRequest(str: String)
  case class ShutdownRequest()
}
