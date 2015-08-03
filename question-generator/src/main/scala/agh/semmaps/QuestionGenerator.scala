package agh.semmaps

object QuestionGenerator {

  /** Any question is characterized by some entropy (how badly it discerns rooms) and a cost (indirectly defined in a configuration file) */
  sealed trait Question[Answer] {
    val answers: Map[Answer, Set[JmlTree]]
    val cost: Double

    final def entropy: Double = {
      val counts = answers map (_._2.size)
      val numAll = counts.sum

      counts.filter(_ > 0).map { count ⇒
        val p = 1.0 / count
        val single = -p * math.log(p) / math.log(2)
        val entr = count * single
        count.toDouble / numAll * entr
      }.sum
    }

    final def overallCost: Double = ??? // TODO: combine entropy and cost somehow (mayhap like in Bobek’s paper)
  }

  /** “Does an object of a given className (and—optionally—such-and-such property) exist in the room?” */
  final case class Exists(className: String, prop: Option[(String, String)], answers: Map[Boolean, Set[JmlTree]], cost: Double) extends Question[Boolean]

  // final case class Count(...) extends Question[Int] // TODO
  // final case class Relation(...) extends Question[???] // TODO

  /** Question generator for given alternatives and costs map */
  type QuestionSetGen = (Set[JmlTree], Map[JmlObject, Double]) ⇒ Set[Question[_]]

  /** Primitively flattens JML trees and compares only classes (in ontological sense) of objects that exists in alternative rooms */
  val SimpleClassExists: QuestionSetGen = { (alternatives, costs) ⇒
    alternatives.map(alternative ⇒
      (flattened(alternative) map (obj ⇒ (obj.className, costs(obj)) → alternative)).toSeq
    ).fold(Seq.empty)(_ ++ _).groupBy(_._1).mapValues(_.map(_._2).toSet).map {
      case ((className, cost), alts) ⇒
        Exists(className, None, Map(true → alts, false → (alternatives -- alts)), cost): Question[_]
    }.toSet
  }

  def apply(alternatives: Set[JmlTree], costRules: Set[Cost]): Unit = {
    // 0. Build the hash map of costs (point of possible optimization)
    val costs = (alternatives map costsMap(costRules)).fold(Map.empty)(_ ++ _)

    // 1. Build the question Set from trees of alternatives
    SimpleClassExists(alternatives, costs) foreach { q ⇒
      println(q)
      println(s"   entropy = ${q.entropy}")
    }

    // 2. Choose a question param with the lowest entropy and cost combined

    // 3. Ask the question

    // 4. Go to 1. considering only matching alternatives.

  }

  def flattened(tree: JmlTree): Set[JmlObject] =
    (tree.children flatMap flattened) + tree.node

  def costsMap(costRules: Set[Cost])(tree: JmlTree): Map[JmlObject, Double] = {
    ??? // TODO
  }

}
