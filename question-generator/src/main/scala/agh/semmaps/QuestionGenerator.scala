package agh.semmaps

object QuestionGenerator {

  def MinAlternatives = 2

  def apply(trees: Set[JmlTree]) = {
    require(trees.size >= MinAlternatives, s"${trees.size} alternative(s) selected -- there need to be at least $MinAlternatives")
  }

}
