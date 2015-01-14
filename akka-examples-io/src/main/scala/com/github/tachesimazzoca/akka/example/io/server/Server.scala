package com.github.tachesimazzoca.akka.example.io.server

import java.net.InetSocketAddress

import akka.actor.{Props, Actor, ActorLogging}
import akka.io.{IO, Tcp}

class Server extends Actor with ActorLogging {
  import akka.io.Tcp._
  import context.system

  IO(Tcp) ! Bind(self, new InetSocketAddress("localhost", 0))

  override def receive: Receive = binding

  private def binding: Receive = {
    case Bound(local) =>
      log.info(s"Bounded ${local}")
      context become connecting

    case CommandFailed(_: Bind) =>
      context stop self
  }

  private def connecting: Receive = {
    case Connected(r, l) =>
      log.info(s"remote ${r} connected to ${l}")
      val handler = context.actorOf(Props[OkayHandler])
      val connection = sender
      connection ! Register(handler)
  }
}
