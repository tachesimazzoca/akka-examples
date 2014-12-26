package com.github.tachesimazzoca.akka.example.kernel

import akka.actor.Actor

import scala.util.Random

object Player {

  case class Start(hands: List[Game.Hand])

  case object Started

  case object Play

  case object Stop

}

class Player extends Actor {

  import Player._

  var hands: List[Game.Hand] = List()

  override def receive: Receive = waiting

  def waiting: Receive = {
    case Start(xs) =>
      hands = xs
      context.become(playing)
      sender ! Started
  }

  def playing: Receive = {
    case Play =>
      if (!hands.isEmpty) sender ! hands(Random.nextInt(hands.size))
      else sender ! Game.None

    case Stop =>
      hands = List()
      context.become(waiting)
  }
}
