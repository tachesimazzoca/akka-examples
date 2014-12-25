package com.github.tachesimazzoca.akka.example.crawler.client

import com.ning.http.client.AsyncHttpClient
import java.util.concurrent.Executor
import scala.concurrent.{Future, Promise}

object AsyncWebClient extends WebClient {
  private val client = new AsyncHttpClient

  def get(url: String)(implicit exec: Executor): Future[Result] = {
    val p = Promise[Result]()

    try {
      val f = client.prepareGet(url).execute

      f.addListener(new Runnable {
        def run {
          val response = f.get
          val status = response.getStatusCode
          if (status / 100 < 4) {
            p.success(Result(url, status, Option(response.getResponseBody)))
          } else p.failure(BadStatus(status))
        }
      }, exec)
    } catch {
      case e: Throwable => p.failure(e)
    }

    p.future
  }

  def shutdown { client.close }
}
