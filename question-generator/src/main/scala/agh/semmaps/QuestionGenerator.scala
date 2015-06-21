package agh.semmaps

object QuestionGenerator {

  sealed trait QuestionParam
  final case class Exists() extends QuestionParam
  final case class Count() extends QuestionParam
  final case class Relation() extends QuestionParam

  def apply(alternatives: Set[JmlTree]): Unit = {
    // 1. Build the Set from trees of alternatives

    // 2. Choose a question param with the lowest entropy and cost combined

    // 3. Ask the question

    // 4. Get rid of non-matching alternatives

    // 5. Loop

  }

  def buildSet(alternative: JmlTree): Set[QuestionParam] = ???

  //  trait ParamSetBuilder {
  //    def apply(alternative: JmlTree): Set[(QuestionParam, Answer)]
  //  }

}
