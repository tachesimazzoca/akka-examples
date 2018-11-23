package com.github.tachesimazzoca.akka.example.stream.keep

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, RunnableGraph, Sink, Source}
import akka.{Done, NotUsed}

import scala.concurrent.Future
import scala.io.StdIn

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("source")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val source: Source[Int, NotUsed] = Source(1 to 3)

    // Each Keep.* is just a function (L, R) => L|R|(L, R)|NotUsed
    val left: RunnableGraph[NotUsed] =
      source.map("left:" + _).toMat(Sink.foreach(println))(Keep.left)
    val right: RunnableGraph[Future[Done]] =
      source.map("right:" + _).toMat(Sink.foreach(println))(Keep.right)
    val both: RunnableGraph[(NotUsed, Future[Done])] =
      source.map("both:" + _).toMat(Sink.foreach(println))(Keep.both)
    val none: RunnableGraph[NotUsed] =
      source.map("none:" + _).toMat(Sink.foreach(println))(Keep.none)

    println("Press ENTER to stop ...")
    //--------------
    left.run()
    right.run().onComplete(_ => println("right...done"))
    both.run()._2.onComplete(_ => println("both...done"))
    none.run()
    //--------------
    StdIn.readLine()
    system.terminate()
  }
}
