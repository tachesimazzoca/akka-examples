package com.github.tachesimazzoca.akka.example.stream.wordcount

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Main {
  implicit val system = ActorSystem("WordCount")
  implicit val materializer = ActorMaterializer()

  def main(args: Array[String]): Unit = {
    val lines = Seq(
      "Concatenates this Flow with the given Source" +
        " so the first element emitted by that source" +
        " is emitted after the last element of this flow.",
      "Transform this stream by applying the given partial function" +
        " to each of the elements on which the function is defined" +
        " as they pass through this processing step.",
      "Concatenate the given Source to this Flow," +
        " meaning that once this Flow’s input is exhausted" +
        " and all result elements have been generated," +
        " the Source’s elements will be produced."
    )

    println("Preparing")
    val source: Source[String, NotUsed] =
      Source.fromIterator(() => lines.toIterator)

    type WordCount = Map[String, Int]

    val mapper: Flow[String, WordCount, NotUsed] =
      Flow[String].map { x =>
        x.split("[ .,;:]").filterNot(_.isEmpty)
          .foldLeft(Map.empty[String, Int]) { (acc, x) =>
          acc.updated(x, acc.getOrElse(x, 0) + 1)
        }
      }

    val reducer: Sink[WordCount, Future[WordCount]] =
      Sink.fold[WordCount, WordCount](Map.empty[String, Int]) { (acc, x) =>
        x.foldLeft(acc) { (a, wc) =>
          val k = wc._1
          val v = wc._2
          a.updated(k, a.getOrElse(k, 0) + v)
        }
      }

    println("Materializing")
    val g: RunnableGraph[Future[WordCount]] =
      source.via(mapper).toMat(reducer)(Keep.right)

    println("Running")
    g.run().foreach(println)
  }
}
