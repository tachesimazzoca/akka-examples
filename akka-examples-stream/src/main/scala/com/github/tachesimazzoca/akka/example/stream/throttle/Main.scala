package com.github.tachesimazzoca.akka.example.stream.throttle

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.stream.{ActorMaterializer, KillSwitches, ThrottleMode}

import scala.concurrent.duration._
import scala.io.StdIn

object Main {
  implicit val system = ActorSystem("Throttle")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def main(args: Array[String]): Unit = {
    val source: Source[Int, NotUsed] = Source.fromIterator(() => Iterator.from(1))
    val (killSwitch, done) = source
      .map(fizzBuzz)
      .throttle(1, 1.second, 1, ThrottleMode.shaping)
      .viaMat(KillSwitches.single)(Keep.right)
      .toMat(Sink.foreach(println))(Keep.both).run()
    done.onComplete(_ => system.terminate())

    println("Running... (Press ENTER to stop)")

    // waiting for an input
    StdIn.readLine()
    println("Stopping...")
    killSwitch.shutdown()
  }

  private def fizzBuzz(n: Int): String =
    if (n % 3 == 0 && n % 5 == 0) "FizzBuzz"
    else if (n % 3 == 0) "Fizz"
    else if (n % 5 == 0) "Buzz"
    else n.toString
}
