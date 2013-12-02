package com.github.tachesimazzoca.akka.example.counter

import akka.actor.Actor

protected class Counter extends Actor {
  def counter(n: Int): Receive = {
    case Counter.Increment => context.become(counter(n + 1))
    case Counter.Get => sender ! Counter.Count(n)
  }

  def receive = counter(0)
}

protected object Counter {
  case class Increment()
  case class Get()
  case class Count(n: Int)
}
