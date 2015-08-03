package agh.semmaps

object QuestionGenerator {

  sealed trait Question[Answer] {
    val answers: Map[Answer, Set[JmlTree]]
  }

  final case class Exists(className: String, prop: Option[(String, String)], answers: Map[Boolean, Set[JmlTree]]) extends Question[Boolean]

  // final case class Count(...) extends Question[Int] // TODO
  // final case class Relation(...) extends Question[???] // TODO

  def apply(alternatives: Set[JmlTree], costs: Set[Cost]): Unit = {
    // 1. Build the Set from trees of alternatives

    val simpleClassExists = alternatives.map(alternative ⇒
      (flattened(alternative) map (_.className → alternative)).toSeq
    ).fold(Seq.empty)(_ ++ _).groupBy(_._1).mapValues(_.map(_._2).toSet).map {
      case (className, alts) ⇒
        Exists(className, None, Map(true → alts, false → (alternatives -- alts))): Question[_]
    }.toSet

    simpleClassExists foreach println

    // już z tego można policzyć entropię, koszty i wybrać najlepsze pytanie:
    //
    // Question_1 → Answer_a → room_x, room_y
    //            → Answer_b → room_z

    // 2. Choose a question param with the lowest entropy and cost combined
    //
    // https://youtu.be/wL9aogTuZw8?t=69

    // 3. Ask the question

    // 4. Get rid of non-matching alternatives

    // 5. Loop

  }

  def flattened(tree: JmlTree): Set[JmlObject] =
    (tree.children flatMap flattened) + tree.node

  //  trait ParamSetBuilder {
  //    def apply(alternative: JmlTree): Set[(QuestionParam, Answer)]
  //  }

}
