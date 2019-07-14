package ru.d10xa.har_cleaner

import java.net.URL

import io.circe.parser._
import better.files._
import io.circe.Json
import pureconfig.error.ConfigReaderFailures

object Main {

  final case class UrlPatterns(
    hostEndsWith: Vector[String] = Vector.empty,
    urlStartsWith: Vector[String] = Vector.empty,
    pathEndsWith: Vector[String] = Vector.empty
  )
  final case class Cfg(
    blacklist: UrlPatterns,
    whitelist: Option[UrlPatterns] = None
  )

  def predicate(c: Cfg, j: Json): Boolean =
    j.hcursor
      .downField("request")
      .downField("url")
      .as[String]
      .map(urlPredicate(c, _)) match {
      case Right(value) => value
      case Left(e) => throw e
    }

  def urlPredicate(config: Cfg, url: String): Boolean = {

    val u = new URL(url)
    val path = u.getPath
    val host = u.getHost

    val hostEndWithMatch =
      config.blacklist.hostEndsWith.exists(str => host.endsWith(str))
    val urlStartsWithMatch =
      config.blacklist.urlStartsWith.exists(str => url.startsWith(str))
    val pathEndsWithMatch =
      config.blacklist.pathEndsWith.exists(x => path.endsWith(x))

    val result = hostEndWithMatch || pathEndsWithMatch || urlStartsWithMatch

    !result
  }

  def entriesFilter(c: Cfg, j: Json): Json = {
    val array: Option[Vector[Json]] = j.asArray
    val x: Option[Vector[Json]] = array.map(_.filter(predicate(c, _)))
    x.map(Json.fromValues(_)).getOrElse(Json.arr())
  }

  def parseArgs(args: List[String]): Map[String, String] =
    args
      .sliding(2, 2)
      .toList
      .map(list => (list.head.substring(2), list.tail.head))
      .toMap

  def main(args: Array[String]): Unit = {
    import pureconfig.generic.auto._
    val c = pureconfig.loadConfig[Cfg] match {
      case Right(v) => v
      case Left(e: ConfigReaderFailures) =>
        throw new IllegalArgumentException(e.toString)
    }
    val config = parseArgs(args.toList)
    val in = config("input")
    val out = File(config("output"))
    require(!out.isDirectory, "output is directory")
    val json = parse(File(in).contentAsString) match {
      case Right(value) => value
      case Left(e) => throw e
    }
    val modified = json.hcursor
      .downField("log")
      .downField("entries")
      .withFocus(entriesFilter(c, _))
      .top
    out.write(modified.get.toString())
  }

}
