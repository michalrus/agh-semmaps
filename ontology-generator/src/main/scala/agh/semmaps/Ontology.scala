package agh.semmaps

import scala.util.{ Success, Try }

final case class Ontology(trees: List[JmlTree])

// TODO: there should be a real ontology model in this app, pro’lly; but whatever =)

object Ontology {
  def fromJmlTrees(trees: List[JmlTree]): Try[Ontology] = Success(Ontology(trees))
}
