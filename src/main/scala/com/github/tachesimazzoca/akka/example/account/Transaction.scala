package com.github.tachesimazzoca.akka.example.account

import akka.actor.{Actor, ActorRef}
import akka.event.LoggingReceive

class Transaction extends Actor {
  import Transaction._

  def receive: Receive = LoggingReceive {
    case Transfer(from, to, amount) =>
      from ! Account.Withdraw(amount)
      context.become(awaitWithdraw(to, amount, sender))
  }

  def awaitWithdraw(to: ActorRef, amount: Int,
      client: ActorRef): Receive = LoggingReceive {
    case Account.Balance(_) =>
      to ! Account.Deposit(amount)
      context.become(awaitDeposit(client))

    case Account.Failed => client ! Failed
  }

  def awaitDeposit(client: ActorRef): Receive = LoggingReceive {
    case Account.Balance(_) => client ! Done
  }
}

object Transaction {
  case class Transfer(from: ActorRef, to: ActorRef, amount: Int)
  case object Done
  case object Failed
}
