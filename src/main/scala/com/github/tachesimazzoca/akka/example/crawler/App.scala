package com.github.tachesimazzoca.akka.example.crawler

import akka.actor.Actor
import akka.actor.Props

object App {
  case class Done(url: String)
}

class App extends Actor {
  import App._

  val receptionist = context.actorOf(Props[Receptionist])

  var urls = Set(
    "http://akka.io/"
  )

  receptionist ! Receptionist.Initialize(maxDepth = 2, maxQueueSize = 3)

  def receive: Receive = {
    case Receptionist.Initialized =>
      urls foreach { url =>
        receptionist ! Receptionist.Get(url)
      }

    case Receptionist.Result(url, results) =>
      println(url)
      println("".padTo(url.length, '='))
      results foreach { case (k, v) =>
        println(v + " " + k)
      }
      self ! Done(url)

    case Receptionist.Failed(url) =>
      println(url + ": failed")
      self ! Done(url)

    case Done(url) =>
      urls -= url
      if (urls.isEmpty) context.stop(self)
  }

  override def postStop() {
    WebClient.shutdown
  }
}
