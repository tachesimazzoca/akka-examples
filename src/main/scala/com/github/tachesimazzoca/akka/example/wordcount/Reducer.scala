package com.github.tachesimazzoca.akka.example.wordcount

import akka.actor.Actor

protected class Reducer extends Actor {
  def receive = {
    case m: Reducer.Input =>
      sender ! Reducer.Output(m.key, m.values.sum)
    case m @ _ =>
      sender ! m
  }
}

protected object Reducer {
  case class Input(val key: String, val values: List[Int])
  case class Output(val key: String, val value: Int)
}
