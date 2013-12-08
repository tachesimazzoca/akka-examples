package com.github.tachesimazzoca.akka.example.wordcount

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

import akka.testkit.ImplicitSender
import akka.testkit.TestKit

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSuiteLike

class ActorSuite extends TestKit(ActorSystem("ActorSuite"))
  with FunSuiteLike with BeforeAndAfterAll
  with ImplicitSender {

  override def afterAll() {
    system.shutdown
  }

  test("mapper returns word maps") {
    val mapper = system.actorOf(Props[Mapper])
    mapper ! "foo bAr, baz.FOO. Baz"
    val expected = List(
      ("foo", 1),
      ("bar", 1),
      ("baz", 1),
      ("foo", 1),
      ("baz", 1)
    )
    expectMsg(Mapper.Output(expected))
  }

  test("reducer returns word counts") {
    val reducer = system.actorOf(Props[Reducer])
    reducer ! Reducer.Input("foo", List(1, 2, 3))
    expectMsg(Reducer.Output("foo", 6))
  }
}
