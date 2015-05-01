package agh.semmaps

import java.io.File

import scopt.OptionParser

final case class Config(inputDirectory: File, output: File, owl: Boolean)

object Config {

  def apply(args: List[String]): Option[Config] = {
    val p = new OptionParser[Config]("question-generator") {
      head("question-generator")
      opt[File]('i', "input-directory") required () valueName "<file>" action { (x, c) ⇒ c.copy(inputDirectory = x) } text "an input directory with *.jml files"
      opt[File]('o', "output") required () valueName "<file>" action { (x, c) ⇒ c.copy(output = x) } text "an output file that’ll hold the ontology"
      opt[Unit]("owl") action { (_, c) ⇒ c.copy(owl = true) } text "should we output in OWL format?"
      help("help") abbr "h" text "display this help and exit"
    }

    p.parse(args, Config(new File(""), new File(""), owl = false))
  }

}
