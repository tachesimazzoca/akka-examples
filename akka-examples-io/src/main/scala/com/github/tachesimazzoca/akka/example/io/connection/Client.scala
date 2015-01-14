package com.github.tachesimazzoca.akka.example.io.connection

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, Props}
import akka.io.{IO, Tcp}

object Client {
  def props(remote: InetSocketAddress, replies: ActorRef) =
    Props(classOf[Client], remote, replies)
}

class Client(remote: InetSocketAddress, listener: ActorRef) extends Actor {
  import akka.io.Tcp._
  import context.system // load implicit actor system

  IO(Tcp) ! Connect(remote)
  override def receive: Receive = connecting

  private def connecting: Receive = {
    case cf @ CommandFailed(con) =>
      listener ! cf
      context stop self

    case c @ Connected(r, l) =>
      listener ! c
      sender ! Close
      context become closing
  }

  private def closing: Receive = {
    case cc: ConnectionClosed =>
      listener ! cc
      context stop self
  }
}
