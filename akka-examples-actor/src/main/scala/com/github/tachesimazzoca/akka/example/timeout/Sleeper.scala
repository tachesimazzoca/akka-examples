package com.github.tachesimazzoca.akka.example.timeout

import akka.actor.Actor
import akka.actor.Props

class Sleeper extends Actor {
  def receive: Receive = {
    case (delay: Long, msg: Any) =>
      context.actorOf(Props(new Actor {
        def receive: Receive = {
          case (d: Long, m: Any) =>
            Thread.sleep(d)
            sender ! m
        }
      })) forward (delay, msg)
  }
}
