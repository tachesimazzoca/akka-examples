package com.github.tachesimazzoca.akka.example.account

import akka.actor.Actor
import akka.event.LoggingReceive

class Account extends Actor {
  import Account._

  private var balance = 0

  def receive: Receive = LoggingReceive {
    case Deposit(amount) =>
      balance += amount
      sender ! Balance(balance)
    case Withdraw(amount) if (amount <= balance) =>
      balance -= amount
      sender ! Balance(balance)
    case _ =>
      sender ! Failed
  }
}

object Account {
  case class Deposit(amount: Int) { require(amount > 0) }
  case class Withdraw(amount: Int) { require(amount > 0) }
  case class Balance(amount: Int) { require(amount > 0) }
  case object Failed
}
