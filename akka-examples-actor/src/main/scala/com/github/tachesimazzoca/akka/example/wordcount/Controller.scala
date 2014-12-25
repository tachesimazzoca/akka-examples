package com.github.tachesimazzoca.akka.example.wordcount

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props

object Controller {
  case class Request(lines: Seq[String])
  case class Result(entries: List[Reducer.Output])
}

class Controller extends Actor {
  import Controller._

  private var mappers = Set.empty[ActorRef]
  private var reducers = Set.empty[ActorRef]

  def receive: Receive = waiting

  def waiting: Receive = {
    case Request(lines) =>
      lines foreach { x =>
        val mapper = context.actorOf(Props[Mapper])
        mappers += mapper
        mapper ! x
      }
      context.become(mapping(Nil))
  }

  def mapping(outputs: List[Mapper.Output]): Receive = {
    case output: Mapper.Output =>
      mappers -= sender
      if (mappers.isEmpty) {
        val inputs = (
          for {
            x <- output :: outputs
            y <- x.entries
          } yield (y._1, y._2)
        ) groupBy(_._1) map { case (k, v) =>
          Reducer.Input(k, v.unzip._2)
        }
        inputs foreach { x =>
          val reducer = context.actorOf(Props[Reducer])
          reducers += reducer
          reducer ! x
        }
        context.become(reducing(Nil))
      } else context.become(mapping(output :: outputs))
  }

  def reducing(outputs: List[Reducer.Output]): Receive = {
    case output: Reducer.Output =>
      reducers -= sender
      if (reducers.isEmpty) {
        val entries = (output :: outputs).sortWith(_.value > _.value)
        context.parent ! Result(entries)
        context.become(waiting)
      } else context.become(reducing(output :: outputs))
  }
}
