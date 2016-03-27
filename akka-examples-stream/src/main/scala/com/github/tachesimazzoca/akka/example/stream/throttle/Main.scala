package com.github.tachesimazzoca.akka.example.stream.throttle

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.stream.{ActorMaterializer, ThrottleMode}

import scala.concurrent.duration._

object Main {
  implicit val system = ActorSystem("Throttle")
  implicit val materializer = ActorMaterializer()

  def main(args: Array[String]): Unit = {
    val source: Source[Int, NotUsed] =
      Source.fromIterator(() => Iterator from 1)
    source
      .map(fizzBuzz)
      .throttle(1, 1.second, 1, ThrottleMode.shaping)
      .runForeach(println)
  }

  private def fizzBuzz(n: Int): String =
    if (n % 3 == 0 && n % 5 == 0) "FizzBuzz"
    else if (n % 3 == 0) "Fizz"
    else if (n % 5 == 0) "Buz"
    else n.toString
}
