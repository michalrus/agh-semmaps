package agh.semmaps

object QuestionGenerator {

  sealed trait QuestionParam
  final case class Exists() extends QuestionParam
  final case class Count() extends QuestionParam
  final case class Relation() extends QuestionParam

  def apply(alternatives: Set[JmlTree]) = {
    // 1. Build the Set from trees of alternatives

    // 2. Choose a question param with the lowest entropy

  }

  def buildSet(alternative: JmlTree): Set[QuestionParam] = ???

}
