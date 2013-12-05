package com.github.tachesimazzoca.akka.example.crawler.client

import java.util.concurrent.Executor
import scala.concurrent.Future

case class Result(url: String, status: Int, body: Option[String])
case class BadStatus(status: Int) extends RuntimeException

trait WebClient {
  def get(url: String)(implicit exec: Executor): Future[Result]
}
