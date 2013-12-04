package com.github.tachesimazzoca.akka.example.crawler

import scala.language.postfixOps

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.Terminated
import akka.actor.ReceiveTimeout
import scala.concurrent.duration._

object Controller {
  case class Mark(url: String, status: Int)
  case class Check(url: String, depth: Int)
  case class Result(results: Map[String, Int])
}

class Controller extends Actor {
  import Controller._

  var results = Map.empty[String, Int]
  var visited = Set.empty[String]
  var children = Set.empty[ActorRef]

  context.setReceiveTimeout(10 second)

  def receive: Receive = {
    case Mark(url, status) =>
      results += url -> status
      sender ! Getter.Abort

    case Check(url, depth) =>
      if (!visited(url) && depth > 0) {
        children += context.actorOf(Props(new Getter(url, depth - 1)))
      }
      visited += url

    case Getter.Done =>
      children -= sender
      if (children.isEmpty) context.parent ! Result(results)

    case ReceiveTimeout =>
      context.children foreach (_ ! Getter.Abort)
  }
}
