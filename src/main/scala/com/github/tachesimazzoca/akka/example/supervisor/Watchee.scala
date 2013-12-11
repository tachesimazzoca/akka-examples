package com.github.tachesimazzoca.akka.example.supervisor

import akka.actor.Actor
import akka.actor.ActorRef
import akka.event.LoggingReceive

object Watchee {
  case class Do(f: () => Unit)
  case object Done
  case object Stop
}

class Watchee extends Actor {
  import Watchee._

  override def preStart() {
    println("preStart(): " + self)
  }

  override def postStop() {
    println("postStop(): " + self)
  }

  override def preRestart(reason: Throwable, message: Option[Any]) {
    println("preRestart(): " + self)
    println(" - reason: " + reason)
    println(" - message: " + message)
  }

  override def postRestart(reason: Throwable) {
    println("postRestart(): " + self)
    println(" - reason: " + reason)
  }

  def receive: Receive = LoggingReceive {
    case Stop => context.stop(self)
    case Do(f) => f(); sender ! Done
  }
}
