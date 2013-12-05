package com.github.tachesimazzoca.akka.example.hello

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

import akka.testkit.ImplicitSender
import akka.testkit.TestKit

import org.scalatest.WordSpecLike
import org.scalatest.BeforeAndAfterAll

class GreeterSpec extends TestKit(ActorSystem("GreeterSpec"))
  with WordSpecLike with BeforeAndAfterAll with ImplicitSender {

  override def afterAll() {
    system.shutdown()
  }

  "A Greeter" must {
    "return the message 'Hello'" in {
      val greeter = system.actorOf(Props[Greeter])
      greeter ! Greeter.Request
      expectMsg(Greeter.Response("Hello"))
    }
  }
}
