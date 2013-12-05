package com.github.tachesimazzoca.akka.example.crawler

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props

import akka.testkit.ImplicitSender
import akka.testkit.TestKit

import org.scalatest.BeforeAndAfterAll
import org.scalatest.WordSpecLike

import scala.concurrent.Future
import java.util.concurrent.Executor

import com.github.tachesimazzoca.akka.example.crawler.client._

class StepParent(child: Props, fwd: ActorRef) extends Actor {
  context.actorOf(child, "child")
  def receive: Receive = {
    case msg => fwd.tell(msg, sender)
  }
}

object GetterSpec {
  val okURL = "http://akka.io/"

  val pages = Map(
    okURL -> (
      """<html>
        |  <head><title>OK</title></head>
        |  <body>
        |    <a href="./link.html">click here</a>
        |  </body>
        |</html>""".stripMargin,
      Seq("http://akka.io/link.html")
    )
  )

  object FakeWebClient extends WebClient {
    def get(url: String)(implicit exec: Executor): Future[Result] =
      pages.get(url) match {
        case None => Future.failed(BadStatus(404))
        case Some((x, _)) => Future.successful(Result(url, 200, Option(x)))
      }
  }

  def fakeGetter(url: String, depth: Int): Props =
    Props(new Getter(url, depth) {
      override def client: WebClient = FakeWebClient
    })
}

class GetterSpec extends TestKit(ActorSystem("GetterSpec"))
  with WordSpecLike with BeforeAndAfterAll with ImplicitSender {

  import GetterSpec._

  override def afterAll(): Unit = {
    system.shutdown()
  }

  "A Getter" must {
    "mark the OK URL as 200 and check the links" in {
      val depth = 2
      val getter = system.actorOf(Props(new StepParent(fakeGetter(okURL, depth), testActor)), "okURL")
      for (link <- pages(okURL)._2) {
        expectMsg(Controller.Check(link, depth))
      }
      expectMsg(Controller.Mark(okURL, 200))
    }

    "mark the NotFound URL as 404" in {
      val url = "notfound"
      val getter = system.actorOf(Props(new StepParent(fakeGetter(url, 2), testActor)))
      expectMsg(Controller.Mark(url, 404))
    }
  }
}
