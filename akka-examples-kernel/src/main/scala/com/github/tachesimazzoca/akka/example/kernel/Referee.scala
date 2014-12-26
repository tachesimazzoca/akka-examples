package com.github.tachesimazzoca.akka.example.kernel

import akka.actor.{Actor, ActorRef, Props}

object Referee {
  case object Start
  case object Play
}

class Referee extends Actor {

  import com.github.tachesimazzoca.akka.example.kernel.Referee._

  val player1 = context.actorOf(Props[Player])
  val player2 = context.actorOf(Props[Player])

  var playersRef = Set[ActorRef]()
  var results: (Game.Hand, Game.Hand) = (Game.None, Game.None)

  override def receive: Receive = setup

  def setup: Receive = {
    case Start =>
      player1 ! Player.Start(List(Game.Rock, Game.Scissors, Game.Paper))
      player2 ! Player.Start(List(Game.Rock, Game.Scissors, Game.Paper))
      playersRef = Set(player1, player2)
      context.become(waiting)
  }

  def waiting: Receive = {
    case Player.Started =>
      playersRef -= sender
      if (playersRef.isEmpty) {
        context.become(playing)
        self ! Play
      }
  }

  def playing: Receive = {
    case Play =>
      playersRef = Set(player1, player2)
      results = (Game.None, Game.None)
      player1 ! Player.Play
      player2 ! Player.Play

    case hand: Game.Hand =>
      playersRef -= sender

      if (sender eq player1)
        results = (hand, results._2)
      else if (sender eq player2)
        results = (results._1, hand)

      if (playersRef.isEmpty) {
        println(s"Player1: ${results._1} vs. Player2: ${results._2}")
        if (results._1 == results._2) self ! Play
        else context.stop(self)
      }
  }
}
