package com.github.tachesimazzoca.akka.example.wordcount

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props

import scala.collection.mutable.ListBuffer

class App extends Actor with ActorLogging {
  private val lines = List(
    "Foo Bar Buz."
  , "Bar. Buz."
  , "Fuga foo hoge, .... buz."
  , "FOO, foo, FoO . "
  )
  private val mapOutputs = new ListBuffer[Mapper.Output]
  private val reduceOutputs = new ListBuffer[Reducer.Output]
  private var sizeOfReduceInputs: Int = 0

  override def preStart() {
    mapOutputs.clear()
    reduceOutputs.clear()
    if (lines.size > 0) {
      val mapper = context.actorOf(Props[Mapper])
      lines.foreach(mapper ! _)
    } else {
      context.stop(self)
    }
  }

  def receive: Receive = {
    case mo: Mapper.Output =>
      mapOutputs += mo
      if (mapOutputs.size == lines.size) {
        val words = new ListBuffer[(String, Int)]
        mapOutputs.foreach(words ++= _.entries)
        val inputs = toReduceInputs(List(), words.toList)
        sizeOfReduceInputs = inputs.size
        if (lines.size > 0) {
          val reducer = context.actorOf(Props[Reducer])
          inputs.foreach(reducer ! _)
        } else {
          context.stop(self)
        }
      }
    case mo: Reducer.Output =>
      reduceOutputs += mo
      if (reduceOutputs.size == sizeOfReduceInputs) {
        reduceOutputs.toList.sortWith(_.value > _.value).foreach { o =>
          println("%s: %d".format(o.key, o.value))
        }
        context.stop(self)
      }
    case _ =>
      log.info("Unknown message")
  }

  def toReduceInputs(a: List[Reducer.Input],
      entries: List[(String, Int)]): List[Reducer.Input] = {
    entries match {
      case Nil => a
      case x :: xs =>
        val (inputs, remain) = xs partition (_._1 == x._1)
        toReduceInputs(Reducer.Input(x._1, (x +: inputs).unzip._2) :: a, remain)
    }
  }
}
