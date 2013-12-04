package com.github.tachesimazzoca.akka.example.crawler

import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class GetterTest extends FunSuite {
  test("Getter.normalizeURL") {
    assert(
      Getter.normalizeURL("http://example.net/", "index.html") ===
      Some("http://example.net/index.html")
    )
    assert(
      Getter.normalizeURL("https://example.net/a.html", "./index.html") ===
      Some("https://example.net/index.html")
    )
    assert(
      Getter.normalizeURL("http://example.net/a/", "../index.html") ===
      Some("http://example.net/index.html")
    )
    assert(
      Getter.normalizeURL("https://example.net/a/b.html", "../index.html") ===
      Some("https://example.net/index.html")
    )
    assert(
      Getter.normalizeURL("http://example.net/a/b.html", "mailto:foo@example.net") ===
      None
    )
    assert(
      Getter.normalizeURL("ftp://example.net/", "a.txt") ===
      None
    )
    assert(
      Getter.normalizeURL("http://js.example.net/", "javascript:void();") ===
      None
    )
  }
}
