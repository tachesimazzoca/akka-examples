package com.github.tachesimazzoca.akka.example.timeout

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import akka.actor.Status
import akka.pattern.ask
import akka.pattern.pipe
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._

class Main extends Actor with ActorLogging {
  import context.dispatcher

  implicit val timeout = Timeout(1.second)

  val launchTime = System.currentTimeMillis

  val sleeper = context.actorOf(Props[Sleeper])

  type Operations = Vector[Future[String]]

  var pending: Operations = Vector()

  val safeOperation = {
    val xf = (sleeper ? (500L, "v1")).mapTo[String]
    val yf = (sleeper ? (500L, "v2")).mapTo[String]
    for {
      x <- xf
      y <- yf
      a <- (sleeper ? (500L, s"[$x, $y]")).mapTo[String]
      b <- (sleeper ? (500L, s"[$x, $y, $a]")).mapTo[String]
    } yield s"$x, $y, $a, $b"
  }

  val timeoutOperation = {
    (sleeper ? (1500L, "timeout")).mapTo[String]
  }

  self ! Vector(safeOperation, timeoutOperation)

  def receive: Receive = {
    case ops: Operations =>
      pending = ops
      nextQueue()

    case msg: String =>
      log.info(msg + ": " + (System.currentTimeMillis - launchTime) + " msec")
      nextQueue()

    case Status.Failure(e: Throwable) =>
      log.info("failed: " + e)
      nextQueue()
  }

  private def nextQueue() {
    if (pending.isEmpty) context.stop(self)
    else {
      val queue = pending.head
      pending = pending.tail
      queue pipeTo self
    }
  }
}
