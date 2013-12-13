package com.github.tachesimazzoca.akka.example.crawler

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Status
import akka.pattern.pipe
import java.net.URI
import java.util.concurrent.Executor
import scala.concurrent.ExecutionContext
import scala.util.{Try, Success}

import com.github.tachesimazzoca.akka.example.crawler.client._

object Getter {
  case object Done
  case object Abort

  private val A_TAG = "(?i)<a ([^>]+)>.+?</a>".r
  private val HREF_ATTR = """\s*(?i)href\s*=\s*(?:"([^"]*)"|'([^']*)'|([^'">\s]+))\s*""".r

  def parseLinks(body: String): Iterator[String] = {
    for {
      anchor <- A_TAG.findAllMatchIn(body)
      HREF_ATTR(dquot, quot, bare) <- anchor.subgroups
    } yield if (dquot != null) dquot
    else if (quot != null) quot
    else bare
  }

  def normalizeURL(base: String, href: String): Option[String] = {
    val url = for {
      hrefURI <- Try(new URI(href))
      baseURI <- Try(new URI(base))
    } yield {
      if (hrefURI.isAbsolute) href
      else baseURI.resolve(hrefURI).toURL.toString
    }
    url.filter(_.matches("^https?://.+$")).toOption
  }
}

class Getter(url: String, depth: Int) extends Actor with ActorLogging {
  import Getter._

  implicit val exec =
      context.dispatcher.asInstanceOf[Executor with ExecutionContext]

  def client: WebClient = AsyncWebClient

  client.get(url).pipeTo(self)

  def receive: Receive = {
    case result: Result =>
      for (
        x <- result.body;
        uri <- parseLinks(x);
        link <- normalizeURL(url, uri)
      ) {
        context.parent ! Controller.Check(link, depth)
      }
      context.parent ! Controller.Mark(url, result.status)

    case Status.Failure(BadStatus(status)) =>
      context.parent ! Controller.Mark(url, status)

    case Done => stop()
    case Abort =>
      log.info("Aborted: " + self)
      stop()
  }

  def stop() { context.stop(self) }
}
