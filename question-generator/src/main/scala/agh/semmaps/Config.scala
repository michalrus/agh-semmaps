package agh.semmaps

import java.io.File

import scopt.OptionParser

final case class Config(inputDirectory: File,
                        costsFile: File,
                        classNameKey: String,
                        splitKeys: Set[String], splitDelimiters: List[Char],
                        prologOntology: Option[File],
                        alternatives: Set[(String, String)])

object Config {

  def apply(args: List[String]): Option[Config] = {
    val p = new OptionParser[Config]("question-generator") {
      head("question-generator")
      opt[File]('i', "input-directory") required () valueName "<file>" action { (x, c) ⇒ c.copy(inputDirectory = x) } text "an input directory with *.jml files"
      opt[String]('k', "class-name-key") required () valueName "<key>" action { (x, c) ⇒ c.copy(classNameKey = JmlParser.sanitizeKeys(x)) } text "a GML feature key, values of which will be used as the source for object class names"
      opt[File]('c', "costs-file") required () valueName "<file>" action { (x, c) ⇒ c.copy(costsFile = x) } text "question costs definitions for a given map"
      opt[Seq[String]]('s', "split-keys") valueName "<key1>,<key2>,..." action { (x, c) ⇒ c.copy(splitKeys = x.toSet map JmlParser.sanitizeKeys) } text "GML feature keys, values of which will be splitted into boolean attributes"
      opt[String]('d', "split-delimiters") valueName "<delim1><delim2>..." action { (x, c) ⇒ c.copy(splitDelimiters = x.toCharArray.toList) } text "delimiter characters used for --split-keys"
      opt[File]("prolog-ontology") valueName "<file>" action { (x, c) ⇒ c.copy(prologOntology = Some(x)) } text "an optional output file to hold the intermediate ontology"
      opt[String]('a', "alternative") minOccurs 1 maxOccurs 1024 valueName "<key>=<value>" validate { x ⇒ if (x contains '=') success else failure("an --alternative has to be given as <key>=<value>") } action {
        (x, c) ⇒
          val n = x.indexOf('=')
          c.copy(alternatives = c.alternatives + ((x take n, x drop (n + 1))))
      } text "alternatives obtained from sensor fusion mechanism; <key> is a GML key, e.g. \"number\" and <value> is its value, e.g. \"316\" (if specify more than one --alternative, they will be ORed)"
      help("help") abbr "h" text "display this help and exit"
    }

    p.parse(args, Config(new File(""), new File(""), "class", Set.empty, Nil, None, Set.empty))
  }

}
