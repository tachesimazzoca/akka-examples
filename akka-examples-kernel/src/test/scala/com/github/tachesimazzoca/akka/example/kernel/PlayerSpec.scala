package com.github.tachesimazzoca.akka.example.kernel

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class PlayerSpec extends TestKit(ActorSystem("GreeterSpec"))
                         with WordSpecLike
                         with BeforeAndAfterAll
                         with ImplicitSender {

  override def afterAll() {
    system.shutdown()
  }

  "A Player" must {
    "return Player.Started after receiving Player.Start" in {
      val player = system.actorOf(Props[Player])
      player ! Player.Start(List[Game.Hand]())
      expectMsg(Player.Started)
    }

    "always return Game.None if the hands is empty" in {
      val player = system.actorOf(Props[Player])
      player ! Player.Start(List[Game.Hand]())
      expectMsg(Player.Started)
      player ! Player.Play
      expectMsg(Game.None)
    }

    "return one of the hands" in {
      val player = system.actorOf(Props[Player])
      player ! Player.Start(List[Game.Hand](Game.Rock, Game.Scissors, Game.Paper))
      expectMsg(Player.Started)
      for (n <- 1 to 10) {
        player ! Player.Play
        expectMsgAnyOf(Game.Rock, Game.Scissors, Game.Paper)
      }
    }

    "not play after receiving Player.Stop" in {
      val player = system.actorOf(Props[Player])
      player ! Player.Start(List[Game.Hand](Game.Rock, Game.Scissors, Game.Paper))
      expectMsg(Player.Started)
      player ! Player.Play
      expectMsgType[Game.Hand]
      player ! Player.Stop
      expectNoMsg(5 seconds)
    }
  }
}
