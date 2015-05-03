package agh.semmaps

import java.io.File

import scopt.OptionParser

final case class Config(inputDirectory: File,
                        splitKeys: Set[String], splitDelimiters: List[Char],
                        prologOntology: Option[File], prologKey: Option[String],
                        alternatives: Set[(String, String)])

object Config {

  def apply(args: List[String]): Option[Config] = {
    val p = new OptionParser[Config]("question-generator") {
      head("question-generator")
      opt[File]('i', "input-directory") required () valueName "<file>" action { (x, c) ⇒ c.copy(inputDirectory = x) } text "an input directory with *.jml files"
      opt[Seq[String]]('s', "split-keys") valueName "<key1>,<key2>,..." action { (x, c) ⇒ c.copy(splitKeys = x.toSet map JmlParser.sanitizeKeys) } text "GML feature keys, values of which will be splitted into boolean attributes"
      opt[String]('d', "split-delimiters") valueName "<delim1><delim2>..." action { (x, c) ⇒ c.copy(splitDelimiters = x.toCharArray.toList) } text "delimiter characters used for --split-keys"
      opt[File]("prolog-ontology") valueName "<file>" action { (x, c) ⇒ c.copy(prologOntology = Some(x)) } text "an optional output file to hold the intermediate ontology"
      opt[String]("prolog-key") valueName "<key>" action { (x, c) ⇒ c.copy(prologKey = Some(JmlParser.sanitizeKeys(x))) } text "a GML feature key, values of which will be used as the source for term names in the Prolog ontology"
      opt[String]('a', "alternative") minOccurs 1 maxOccurs 1024 valueName "<key>=<value>" validate { x ⇒ if (x contains '=') success else failure("an --alternative has to be given as <key>=<value>") } action {
        (x, c) ⇒
          val n = x.indexOf('=')
          c.copy(alternatives = c.alternatives + ((x take n, x drop (n + 1))))
      } text "alternatives obtained from sensor fusion mechanism; <key> is a GML key, e.g. \"number\" and <value> is its value, e.g. \"316\" (if specify more than one --alternative, they will be ORed)"
      help("help") abbr "h" text "display this help and exit"
      checkConfig(c ⇒ if (c.prologKey.isDefined == c.prologOntology.isDefined) success else failure("you've got to have both --prolog-key and --prolog-ontology either defined or undefined"))
    }

    p.parse(args, Config(new File(""), Set.empty, Nil, None, None, Set.empty))
  }

}
