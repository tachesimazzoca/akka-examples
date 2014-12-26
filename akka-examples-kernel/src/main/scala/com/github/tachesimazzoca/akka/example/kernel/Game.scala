package com.github.tachesimazzoca.akka.example.kernel

object Game {

  trait Hand

  case object None extends Hand

  case object Rock extends Hand

  case object Scissors extends Hand

  case object Paper extends Hand

}
