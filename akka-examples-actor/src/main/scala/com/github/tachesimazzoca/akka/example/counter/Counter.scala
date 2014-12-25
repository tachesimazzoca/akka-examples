package com.github.tachesimazzoca.akka.example.counter

import akka.actor.Actor

protected class Counter extends Actor {
  private def counter(n: Int): Receive = {
    case Counter.Increment => context.become(counter(n + 1))
    case Counter.Get => sender ! Counter.Count(n)
  }

  def receive: Receive = counter(0)
}

protected object Counter {
  case object Increment
  case object Get
  case class Count(n: Int)
}
