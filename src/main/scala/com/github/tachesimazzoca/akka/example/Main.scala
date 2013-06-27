package com.github.tachesimazzoca.akka.example

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.ExtendedActorSystem
import akka.actor.Props
import akka.actor.Terminated
import scala.util.control.NonFatal

object Main {
  def main(args: Array[String]): Unit = {
    if (args.length == 1) {
      val system = ActorSystem("Main")
      try {
        val appClass = system.asInstanceOf[ExtendedActorSystem]
            .dynamicAccess.getClassFor[Actor](args(0)).get
        val app = system.actorOf(Props(appClass))
        // Akka 2.1
        val terminator = system.actorOf(Props(new Terminator(app)), "app-terminator")
        // Akka 2.2
        //val terminator = system.actorOf(Props(classOf[Terminator], app), "app-terminator")
      } catch {
        case NonFatal(e) => system.shutdown(); throw e
      }
    } else {
      println("Usage : sbt run <class.of.supervisor.actor>")
    }
  }

  class Terminator(app: ActorRef) extends Actor with ActorLogging {
    context watch app
    def receive = {
      case Terminated(_) =>
        log.info("application supervisor has terminated, shutting down")
        context.system.shutdown()
    }
  }
}
