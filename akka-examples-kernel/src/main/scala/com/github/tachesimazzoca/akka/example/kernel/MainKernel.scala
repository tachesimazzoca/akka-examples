package com.github.tachesimazzoca.akka.example.kernel

import akka.actor.{Actor, Props, ActorSystem}
import akka.kernel.Bootable

class MainKernel extends Bootable {
  val system = ActorSystem("kernel-main")

  override def startup(): Unit = {
    system.actorOf(Props[Referee]) ! Referee.Start
  }

  override def shutdown(): Unit = {
    system.shutdown()
  }
}
