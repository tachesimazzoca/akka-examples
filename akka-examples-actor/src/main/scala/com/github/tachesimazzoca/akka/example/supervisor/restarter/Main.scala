package com.github.tachesimazzoca.akka.example.supervisor.restarter

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props

import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy
import akka.actor.Terminated

import com.github.tachesimazzoca.akka.example.supervisor.Watchee

class Main extends Actor {
  var restarts = Map.empty[ActorRef, Int].withDefaultValue(0)

  override val supervisorStrategy = OneForOneStrategy() {
    case _: ArithmeticException =>
      restarts(sender) match {
        case toomany if toomany >= 3 =>
          println("Stop ...")
          restarts -= sender
          SupervisorStrategy.Stop
        case n =>
          println("Restart for " + n + " error(s)")
          restarts = restarts.updated(sender, n + 1)
          SupervisorStrategy.Restart
      }
  }

  val watchee1 = context.actorOf(Props[Watchee], "watchee1")
  val watchee2 = context.actorOf(Props[Watchee], "watchee2")
  var retry = 3

  self ! ("retry", 0L)

  def receive: Receive = {
    case (a: String, b: Long) =>
      context.actorOf(Props(new Actor {
        def receive: Receive = {
          case (msg: String, msec: Long) =>
            Thread.sleep(msec)
            sender ! msg
        }
      })) forward (a, b)

    case "retry" =>
      retry -= 1
      watchee1 ! Watchee.Do(() => { println("1 / 0 = " + (1 / 0)) })
      watchee2 ! Watchee.Do(() => { println("1 / 1 = 1") })
      if (retry >= 0) self ! ("retry", 500L)
      else self ! ("done", 1000L)

    case "done" => context.stop(self)
  }
}
