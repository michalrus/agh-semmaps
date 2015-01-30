package agh.semmaps

import java.io.File

import scopt.OptionParser

final case class Config(input: File, output: File, owl: Boolean)

object Config {

  def apply(args: List[String]): Option[Config] = {
    val p = new OptionParser[Config]("ontology-generator") {
      head("ontology-generator")
      opt[File]('i', "input") required () valueName "<file>" action { (x, c) ⇒ c.copy(input = x) } text "a GML input file"
      opt[File]('o', "output") required () valueName "<file>" action { (x, c) ⇒ c.copy(output = x) }
      opt[Unit]("owl") action { (_, c) ⇒ c.copy(owl = true) } text "should we output in OWL format?"
      help("help") abbr "h" text "display this help and exit"
    }

    p.parse(args, Config(new File(""), new File(""), owl = false))
  }

}
