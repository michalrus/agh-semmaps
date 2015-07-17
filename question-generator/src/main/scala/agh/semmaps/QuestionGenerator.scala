package agh.semmaps

object QuestionGenerator {

  sealed trait Question {
    val alternative: JmlTree
  }

  final case class Exists(obj: JmlObject, alternative: JmlTree) extends Question

  // final case class Count(...) extends QuestionParam // TODO
  // final case class Relation(...) extends QuestionParam // TODO

  def apply(alternatives: Set[JmlTree]): Unit = {
    // 1. Build the Set from trees of alternatives
    val inp: Set[Set[Question]] = alternatives map (alt ⇒ allOf(alt) map (obj ⇒ Exists(obj, alt): Question))


    // 2. Choose a question param with the lowest entropy and cost combined

    // 3. Ask the question

    // 4. Get rid of non-matching alternatives

    // 5. Loop

  }

  def allOf(tree: JmlTree): Set[JmlObject] =
    (tree.children flatMap allOf) + tree.node

//  trait ParamSetBuilder {
//    def apply(alternative: JmlTree): Set[(QuestionParam, Answer)]
//  }

}
