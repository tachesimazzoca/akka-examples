package com.github.tachesimazzoca.akka.example.supervisor.terminator

import akka.actor.Actor
import akka.actor.Props
import akka.actor.Terminated

import com.github.tachesimazzoca.akka.example.supervisor.Watchee

class Main extends Actor {
  val watchee1 = context.actorOf(Props[Watchee], "watchee1")
  val watchee2 = context.actorOf(Props[Watchee], "watchee2")

  context.watch(watchee1)
  context.watch(watchee2)
  println("context.children: " + context.children)
  watchee1 ! Watchee.Stop

  def receive: Receive = {
    case m @ Terminated(ref) =>
      println("Terminated(" + ref +
        ", existenceConfirmed=" + m.existenceConfirmed +
        ", addressTerminated=" + m.addressTerminated + ")")
      println("context.children: " + context.children)
      // Watch until watchee1 exists
      if (m.existenceConfirmed) context.watch(watchee1)
      else context.stop(self)
  }
}
