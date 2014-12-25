package com.github.tachesimazzoca.akka.example.wordcount

import akka.actor.Actor

object Mapper {
  case class Output(val entries: List[(String, Int)])
}

class Mapper extends Actor {
  def receive: Receive = {
    case str: String =>
      val entries = str.split(Array(' ', ',', '.'))
        .map(_.toLowerCase)
        .filter(_.nonEmpty)
        .map(_ -> 1)
        .toList
      sender ! Mapper.Output(entries)
  }
}
