package com.github.tachesimazzoca.akka.example.io.server

import akka.actor._

import scala.util.control.NonFatal

object Main {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Main")
    val app = system.actorOf(Props[Server])
    try {
      system.actorOf(Props(classOf[Terminator], app), "app-terminator")
    } catch {
      case NonFatal(e) => system.shutdown(); throw e
    }
  }

  class Terminator(app: ActorRef) extends Actor with ActorLogging {
    context watch app

    def receive: Receive = {
      case Terminated(_) =>
        log.info("application supervisor has terminated, shutting down")
        context.system.shutdown()
    }
  }
}
