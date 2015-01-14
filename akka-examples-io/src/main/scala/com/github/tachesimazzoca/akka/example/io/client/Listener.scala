package com.github.tachesimazzoca.akka.example.io.client

import java.net.InetSocketAddress

import akka.actor.{ActorLogging, Actor}
import akka.io.Tcp.{CommandFailed, Connect, Connected, ConnectionClosed}

class Listener extends Actor with ActorLogging {
  var clients = Set(
    context.actorOf(Client.props(new InetSocketAddress("example.net", 80), self))
  )

  override def receive: Receive = {
    case CommandFailed(conn: Connect) =>
      log.info(s"${sender}: CommandFaild ${conn}")
      context stop self

    case Connected(r, l) =>
      log.info(s"${sender}: The local ${l} connected to the remote ${r}")

    case cc: ConnectionClosed =>
      log.info(s"${sender}: ${cc}")
      clients -= sender
      if (clients.isEmpty) context stop self
  }
}
