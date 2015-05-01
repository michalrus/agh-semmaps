package agh.semmaps

import java.io.File

import scopt.OptionParser

final case class Config(inputDirectory: File, prologOntology: Option[File])

object Config {

  def apply(args: List[String]): Option[Config] = {
    val p = new OptionParser[Config]("question-generator") {
      head("question-generator")
      opt[File]('i', "input-directory") required () valueName "<file>" action { (x, c) ⇒ c.copy(inputDirectory = x) } text "an input directory with *.jml files"
      opt[File]("prolog-ontology") valueName "<file>" action { (x, c) ⇒ c.copy(prologOntology = Some(x)) } text "an output file that’ll hold the intermediate ontology"
      help("help") abbr "h" text "display this help and exit"
    }

    p.parse(args, Config(new File(""), None))
  }

}
