package com.github.tachesimazzoca.akka.example.heartbeat

import akka.actor.Actor
import akka.actor.ActorLogging

import scala.concurrent.duration._

object Worker {
  case object HeartBeat
  case class Alived(counter: Int)
}

class Worker extends Actor with ActorLogging {
  import context.dispatcher
  import Worker._

  var counter = 0

  def receive: Receive = alived

  val alived: Receive = {
    case HeartBeat =>
      counter += 1
      log.info(s"HeartBeat #$counter")
      if (counter == 5) {
        log.info("worker is now busy")
        context.become(busy)
        context.system.scheduler.scheduleOnce(10.seconds) {
          log.info("worker is now alived again after 10 seconds")
          context.become(alived)
        }
      }
      sender ! Alived(counter)
  }

  val busy: Receive = {
    case _ => // No reply
  }
}
