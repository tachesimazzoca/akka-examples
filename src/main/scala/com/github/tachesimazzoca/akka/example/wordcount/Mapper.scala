package com.github.tachesimazzoca.akka.example.wordcount

import akka.actor.Actor

import scala.collection.mutable.ListBuffer

protected class Mapper extends Actor {
  def receive: Receive = {
    case str: String =>
      val entries = new ListBuffer[(String, Int)]
      str.split(Array(' ', ',', '.')).map(_.toLowerCase)
          .filter(_.nonEmpty).foreach(entries += _ -> 1)
      sender ! Mapper.Output(entries.toList)
    case m @ _ =>
      sender ! m
  }
}

protected object Mapper {
  case class Output(val entries: List[(String, Int)])
}
