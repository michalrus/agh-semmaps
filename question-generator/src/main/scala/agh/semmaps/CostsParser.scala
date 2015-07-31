package agh.semmaps

import java.io.File
import org.parboiled2._
import scala.util.{ Success, Failure }

final case class Cost(objectSelector: List[Map[String, String]], property: Option[String], change: Int)

object CostsParser {

  final class CParser(val input: ParserInput) extends Parser {
    def AllLines: Rule1[Seq[Cost]] = rule {
      zeroOrMore(OneLine) ~> ((cs: Seq[Option[Cost]]) ⇒ cs.flatten) ~ EOI
    }

    def OneLine: Rule1[Option[Cost]] = rule {
      zeroOrMore(WS) ~ optional(CostDef) ~ zeroOrMore(WS) ~ optional("#" ~ zeroOrMore(CharPredicate.All -- "\n")) ~ "\n"
    }

    def CostDef: Rule1[Cost] = rule {
      ("cost" ~ oneOrMore(WS) ~ MultiSelector ~ optional(Prop) ~ oneOrMore(WS) ~ Change) ~>
        ((os: Seq[Map[String, String]], pr: Option[String], ch: Int) ⇒ Cost(os.toList, pr, ch))
    }

    def MultiSelector: Rule1[Seq[Map[String, String]]] = rule {
      oneOrMore(Selector).separatedBy(".")
    }

    def Selector: Rule1[Map[String, String]] = rule {
      ("[" ~ zeroOrMore(WS) ~ zeroOrMore(PropVal).separatedBy(zeroOrMore(WS) ~ "," ~ zeroOrMore(WS)) ~ zeroOrMore(WS) ~ "]") ~>
        ((kv: Seq[(String, String)]) ⇒ kv.toMap)
    }

    def PropVal: Rule1[(String, String)] = rule {
      Identifier ~ zeroOrMore(WS) ~ "=" ~ zeroOrMore(WS) ~ Identifier ~>
        ((k: String, v: String) ⇒ k → v)
    }

    def Prop: Rule1[String] = rule { "." ~ Identifier }

    def Change: Rule1[Int] = rule { capture(("+" | "-") ~ oneOrMore(CharPredicate.Digit)) ~> ((_: String).toInt) }

    def Identifier: Rule1[String] = rule { capture(oneOrMore(CharPredicate.AlphaNum)) }

    def WS = rule { quiet(anyOf(" \t\n\r")) }
  }

  def apply(file: File): Set[Cost] = {
    val parser = new CParser(io.Source.fromFile(file).mkString)
    parser.AllLines.run() match {
      case Success(costs)           ⇒ costs.toSet
      case Failure(err: ParseError) ⇒ throw new RuntimeException(parser.formatError(err))
      case Failure(err)             ⇒ throw err
    }
  }

}
