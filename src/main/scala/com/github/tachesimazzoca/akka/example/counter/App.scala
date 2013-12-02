package com.github.tachesimazzoca.akka.example.counter

import akka.actor.Actor
import akka.actor.Props

class App extends Actor {
  override def preStart() {
    val counter = context.actorOf(Props[Counter], "counter")
    var n = 0

    new Thread(new Runnable {
      def run() {
        for (_ <- 0 until 10000) {
          counter ! Counter.Increment
          n = n + 1
        }
      }
    }).start()

    new Thread(new Runnable {
      def run() {
        for (_ <- 0 until 10000) {
          counter ! Counter.Increment
          n = n + 1
        }
      }
    }).start()

    Thread.sleep(500L)
    println("Non-atomic: " + n)

    counter ! Counter.Get
  }

  def receive: Receive = {
    case Counter.Count(n: Int) => {
      println("Actor: " + n)
      context.stop(self)
    }
  }
}
