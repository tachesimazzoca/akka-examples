package com.github.tachesimazzoca.akka.example.io.server

import akka.actor.{ActorLogging, Actor}
import akka.util.ByteString

class OkayHandler extends Actor with ActorLogging {
  import akka.io.Tcp._

  override def receive: Receive = {
    case Received(data) =>
      val msg = data.decodeString("UTF-8")
      log.info(s"Received: ${msg}")
      val connection = sender
      connection ! Write(ByteString.fromString("OK\n"))
      context stop self

    case PeerClosed =>
      context stop self
  }
}
