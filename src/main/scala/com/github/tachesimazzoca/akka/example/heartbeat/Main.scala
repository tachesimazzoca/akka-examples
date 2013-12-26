package com.github.tachesimazzoca.akka.example.heartbeat

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import akka.pattern.ask
import akka.pattern.pipe
import akka.pattern.CircuitBreaker
import akka.pattern.CircuitBreakerOpenException
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._

class Main extends Actor with ActorLogging {
  import context.dispatcher

  val breaker = new CircuitBreaker(
    context.system.scheduler,
    maxFailures = 2,
    callTimeout = 100.millis,
    resetTimeout = 5.seconds
  ) onOpen {
    log.info("breaker is now open")
  } onClose {
    log.info("worker is alived. switch to normal operation")
  }

  val worker = context.actorOf(Props[Worker])

  implicit val timeout = Timeout(1.second)

  val heartbeat = context.system.scheduler.schedule(0.nanos, 1.second) {
    breaker.withCircuitBreaker(
      worker ? Worker.HeartBeat
    ).mapTo[Worker.Alived] map { msg =>
      // on success
      msg
    } recover { case err =>
      // on failure
      err
    } pipeTo self
  }

  def receive: Receive = {
    case Worker.Alived(n) =>
      log.info(s"Alived($n)")
      if (n > 10) {
        heartbeat.cancel
        context.stop(self)
      }

    case e =>
      println("failed: " + e)
  }
}
