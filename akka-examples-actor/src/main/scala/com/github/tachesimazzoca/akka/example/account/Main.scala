package com.github.tachesimazzoca.akka.example.account

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import akka.event.LoggingReceive

class Main extends Actor with ActorLogging {
  val accountA = context.actorOf(Props[Account], "accountA")
  val accountB = context.actorOf(Props[Account], "accountB")

  accountA ! Account.Deposit(100)

  def receive: Receive = LoggingReceive {
    case Account.Balance(_) => transfer(84)
  }

  def transfer(amount: Int) {
    val trans = context.actorOf(Props[Transaction], "transaction")
    trans ! Transaction.Transfer(accountA, accountB, amount)
    context.become(LoggingReceive {
      case Transaction.Done =>
        context.stop(self)
      case Transaction.Failed =>
        context.stop(self)
    })
  }
}
