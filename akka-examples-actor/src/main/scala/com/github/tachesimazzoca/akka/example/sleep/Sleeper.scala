package com.github.tachesimazzoca.akka.example.sleep

import akka.actor.Actor
import akka.actor.Props

class Sleeper extends Actor {
  def receive: Receive = {
    case (a: Any, b: Long) =>
      context.actorOf(Props(new Actor {
        def receive: Receive = {
          case (msg: Any, msec: Long) =>
            Thread.sleep(msec)
            sender ! msg
        }
      })) forward (a, b)
    case m @ _ => sender ! m
  }
}
