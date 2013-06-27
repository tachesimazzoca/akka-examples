package com.github.tachesimazzoca.akka.example.sleep

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props

class App extends Actor with ActorLogging {
  override def preStart(): Unit = {
    val sleeper = context.actorOf(Props[Sleeper])
    sleeper ! (PrintRequest("Hello1 after 2sec"), 2000L)
    sleeper ! (PrintRequest("Hello2 after 1sec"), 1000L)
    sleeper ! "Bad message"
    sleeper ! ("Bad message after 3sec", 3000L)
    sleeper ! (ShutdownRequest, 5000L)
  }

  def receive = {
    case PrintRequest(str: String) => println(str)
    case ShutdownRequest => context.stop(self)
    case m @ _ => log.info("Unknown message - " + m)
  }
}

case class PrintRequest(str: String)
case class ShutdownRequest()
