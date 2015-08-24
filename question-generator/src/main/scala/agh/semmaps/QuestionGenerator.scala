package agh.semmaps

object QuestionGenerator {

  /** Any question is characterized by some entropy (how badly it discerns rooms) and a cost (indirectly defined in a configuration file) */
  sealed trait Question {
    type Answer

    val answers: Map[Answer, Set[JmlTree]]
    val cost: Double
    val asText: String
    def asText(a: Answer): String

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

    final def overallCost: Double = cost * (1.0 + entropy)

    /**
     * As we’re choosing the best question based on overall cost (which is entropy and acquisition cost combined),
     * we need a way to eliminate pointless questions. These might have been chosen if the acquisition cost was set favorably.
     */
    final def givesNoInfo: Boolean = answers.count(_._2.nonEmpty) == 1
  }

  /** “Does an object of a given className (and—optionally—such-and-such property) exist in the room?” */
  final case class Exists(className: String, prop: Option[(String, String)], answers: Map[Boolean, Set[JmlTree]], cost: Double) extends Question {
    type Answer = Boolean

    val Vowels = Set('a', 'e', 'i', 'o', 'u')

    val propAsText: String = prop match {
      case None         ⇒ ""
      case Some((k, v)) ⇒ s" $k=$v" // TODO: these rules should be defined in *.rules file
    }

    val asText: String = {
      // a/an — bad idea (“a speakers”, “an uniform” etc.)
      val aAn = "some" //if (Try(className.charAt(0)).map(Vowels.contains) getOrElse false) "an" else "a"

      s"Do you see $aAn$propAsText $className?"
    }

    def asText(a: Boolean): String = if (a) "Yes" else "No"
  }

  // final case class Count(...) extends Question[Int] // TODO
  // final case class Relation(...) extends Question[???] // TODO

  /** Question generator for given alternatives and costs map */
  type QuestionSetGen = (Set[JmlTree], Map[JmlObject, Map[Option[String], Double]]) ⇒ Set[Question]

  /** Primitively flattens JML trees and compares only classes (in ontological sense) of objects that exists in alternative rooms */
  val SimpleClassExists: QuestionSetGen = { (alternatives, costs) ⇒
    alternatives.map(alternative ⇒
      (flattened(alternative) filterNot alternative.node.== map (obj ⇒ (obj.className, costs(obj).getOrElse(None, 0.0)) → alternative)).toSeq
    ).fold(Seq.empty)(_ ++ _).groupBy(_._1).mapValues(_.map(_._2).toSet).map {
      case ((className, cost), alts) ⇒
        Exists(className, None, Map(true → alts, false → (alternatives -- alts)), cost): Question
    }.toSet
  }

  val ClassPropExists: QuestionSetGen = { (alternatives, costs) ⇒
    alternatives.map(alternative ⇒
      (flattened(alternative) filterNot alternative.node.== flatMap (obj ⇒ obj.props.to[Set].map(prop ⇒ (obj.className, prop, {
        val cs = costs(obj)
        cs.getOrElse(None, 0.0) + cs.getOrElse(Some(prop._1), 0.0)
      }) → alternative))).toSeq
    ).fold(Seq.empty)(_ ++ _).groupBy(_._1).mapValues(_.map(_._2).toSet).map {
      case ((className, prop, cost), alts) ⇒
        Exists(className, Some(prop), Map(true → alts, false → (alternatives -- alts)), cost): Question
    }.toSet
  }

  def apply(alternatives: Set[JmlTree], costRules: Set[Cost]): Option[Question] = {
    // 0. Build the map of costs (point of possible optimization)
    val costs = (alternatives map costsMap(costRules)).fold(Map.empty)(_ ++ _)

    def forQuestionSetGen(gen: QuestionSetGen): Option[Question] = {
      // 1. Build the question Set from trees of alternatives
      val allQs = gen(alternatives, costs)

      // 1.1. Filter out questions that, when answered, would give absolutely no additional info.
      //      Question#overallCost combines entropy with acquisition cost and such question could
      //      be chosen.
      val qs = allQs filterNot (_.givesNoInfo)

      // 2. Choose a question with the lowest entropy and cost combined
      if (qs.nonEmpty) Some(qs.minBy(_.overallCost)) else None
    }

    val gensToTry = Stream(SimpleClassExists, ClassPropExists)

    gensToTry.map(forQuestionSetGen).find(_.isDefined).flatten
  }

  def flattened(tree: JmlTree): Set[JmlObject] =
    (tree.children flatMap flattened) + tree.node

  /** Assigns property→cost map to each JmlObject; property==None means “just mentioning the object” */
  def costsMap(costRules: Set[Cost])(tree: JmlTree): Map[JmlObject, Map[Option[String], Double]] = {
    def isSelectorMatching(o: JmlObject, sel: Map[String, String]): Boolean =
      sel forall { case (k, v) ⇒ o.props.get(k) contains v }

    def isMultiSelectorMatching(path: List[JmlObject])(cost: Cost): Boolean =
      (path.reverse zip cost.objectSelector.reverse) forall { case (o, sel) ⇒ isSelectorMatching(o, sel) }

    def loopOverPaths(parents: List[JmlObject])(startAt: JmlTree): Seq[(JmlObject, Option[String], Double)] = {
      val me = startAt.node
      val meWithParents = parents :+ me
      val myCosts = (costRules filter isMultiSelectorMatching(meWithParents)).toSeq map (c ⇒ (me, c.property, c.change.toDouble))
      (startAt.children.toSeq flatMap loopOverPaths(meWithParents)) ++ myCosts
    }

    loopOverPaths(Nil)(tree).groupBy(_._1).mapValues(_.groupBy(_._2).mapValues(_.map(_._3).sum))
  }

}
