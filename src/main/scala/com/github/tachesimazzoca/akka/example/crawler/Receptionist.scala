package com.github.tachesimazzoca.akka.example.crawler

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props

object Receptionist {
  private case class Job(client: ActorRef, url: String)
  case class Initialize(maxDepth: Int, maxQueueSize: Int)
  case object Initialized
  case class Get(url: String)
  case class Result(url: String, results: Map[String, Int])
  case class Failed(url: String)
}

class Receptionist extends Actor {
  import Receptionist._

  var reqNo = 0
  var maxDepth = 0
  var maxQueueSize = 0

  def receive: Receive = setup

  val setup: Receive = {
    case Initialize(depth, queueSize) =>
      reqNo = 0
      maxDepth = depth
      maxQueueSize = queueSize
      sender ! Initialized
      context.become(waiting)
  }

  val waiting: Receive = {
    case Get(url) =>
      context.become(runNext(Vector(Job(sender, url))))
  }

  def running(queue: Vector[Job]): Receive = {
    case Controller.Result(results) =>
      val job = queue.head
      job.client ! Result(job.url, results)
      context.stop(sender)
      context.become(runNext(queue.tail))
    case Get(url) =>
      context.become(enqueueJob(queue, Job(sender, url)))
  }

  def runNext(queue: Vector[Job]): Receive = {
    reqNo += 1
    if (queue.isEmpty) waiting
    else {
      val controller = context.actorOf(Props[Controller], "controller" + reqNo)
      controller ! Controller.Check(queue.head.url, maxDepth)
      running(queue)
    }
  }

  def enqueueJob(queue: Vector[Job], job: Job): Receive = {
    if (queue.size > maxQueueSize) {
      sender ! Failed(job.url)
      running(queue)
    } else running(queue :+ job)
  }
}
