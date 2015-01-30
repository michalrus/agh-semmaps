package agh.semmaps

import scala.util.Try

final case class Ontology()

object Ontology {
  def fromJmlTrees(trees: List[JmlTree]): Try[Ontology] = {
    TreeBuilder.prettyPrint(trees)
    ???
  }
}
