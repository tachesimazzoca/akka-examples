package com.github.tachesimazzoca.akka.example.supervisor.resumer

import akka.actor.Actor
import akka.actor.Props

import akka.actor.SupervisorStrategy
import akka.actor.OneForOneStrategy

import com.github.tachesimazzoca.akka.example.supervisor.Watchee

class Main extends Actor {
  val watchee = context.actorOf(Props[Watchee], "watchee")

  override val supervisorStrategy = OneForOneStrategy() {
    case _: ArithmeticException =>
      println("Resume ...")
      SupervisorStrategy.Resume
  }

  self ! ("invalid", 0L)

  def receive: Receive = {
    case (a: String, b: Long) =>
      context.actorOf(Props(new Actor {
        def receive: Receive = {
          case (msg: String, msec: Long) =>
            Thread.sleep(msec)
            sender ! msg
        }
      })) forward (a, b)

    case "invalid" =>
      watchee ! Watchee.Do(() => { println("1 / 0 = " + (1 / 0)) })
      self ! ("valid", 500L)

    case "valid" =>
      watchee ! Watchee.Do(() => { println("1 / 1 = 1") })
      self ! ("done", 500L)

    case "done" => context.stop(self)
  }
}
