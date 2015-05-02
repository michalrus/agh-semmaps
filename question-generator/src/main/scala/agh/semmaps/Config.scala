package agh.semmaps

import java.io.File

import scopt.OptionParser

final case class Config(inputDirectory: File, prologOntology: Option[File], prologKey: Option[String])

object Config {

  def apply(args: List[String]): Option[Config] = {
    val p = new OptionParser[Config]("question-generator") {
      head("question-generator")
      opt[File]('i', "input-directory") required () valueName "<file>" action { (x, c) ⇒ c.copy(inputDirectory = x) } text "an input directory with *.jml files"
      opt[File]("prolog-ontology") valueName "<file>" action { (x, c) ⇒ c.copy(prologOntology = Some(x)) } text "an optional output file that’ll hold the intermediate ontology"
      opt[String]("prolog-key") valueName "<key>" action { (x, c) ⇒ c.copy(prologKey = Some(JmlParser.sanitizeKeys(x))) } text "a GML feature key, values of which will be used as the source of term names in the Prolog ontology"
      help("help") abbr "h" text "display this help and exit"
      //      arg[String]("<")
      checkConfig(c ⇒ if (c.prologKey.isDefined == c.prologOntology.isDefined) success else failure("you've got to have both --prolog-key and --prolog-ontology either defined or undefined"))
    }

    p.parse(args, Config(new File(""), None, None))
  }

}
