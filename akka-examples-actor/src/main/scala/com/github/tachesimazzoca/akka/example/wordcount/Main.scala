package com.github.tachesimazzoca.akka.example.wordcount

import akka.actor.Actor
import akka.actor.Props

class Main extends Actor {
  val controller = context.actorOf(Props[Controller])

  controller ! Controller.Request(
    Seq(
      "Foo Bar Baz.",
      "Bar. Baz.",
      "Fuga foo hoge, .... baz.",
      "FOO, foo, FoO . "
    )
  )

  def receive: Receive = {
    case Controller.Result(entries) =>
      entries foreach { entry =>
        println("%s: %d".format(entry.key, entry.value))
      }
      context.stop(self)
  }
}
