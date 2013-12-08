package com.github.tachesimazzoca.akka.example.wordcount

import akka.actor.Actor

object Reducer {
  case class Input(val key: String, val values: List[Int])
  case class Output(val key: String, val value: Int)
}

class Reducer extends Actor {
  def receive: Receive = {
    case input: Reducer.Input =>
      sender ! Reducer.Output(input.key, input.values.sum)
  }
}
