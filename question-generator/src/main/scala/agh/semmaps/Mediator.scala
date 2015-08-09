package agh.semmaps

object Mediator {

  def apply(alternativeTrees: Set[JmlTree], costRules: Set[Cost]): Option[JmlTree] = {

    def loopUntilOneLeft(alternatives: Set[JmlTree]): Option[JmlTree] = {
      QuestionGenerator(alternatives, costRules) match {
        case None ⇒
          println("\nNo more discerning questions can be generated.")
          None
        case Some(q) ⇒
          println(s"\n${q.asText}")

          val answers = (q.answers.keys.zipWithIndex map { case (a, i) ⇒ (i + 1, a) }).toMap

          // FIXME: choose only answers that point to at least one room, ignore empty ones

          val answerIdx = Stream continually {
            answers map { case (k, v) ⇒ s"  $k: ${q.asText(v)}" } foreach println
            print("> ")
            val i = try { Some(io.StdIn.readInt()) } catch { case _: NumberFormatException ⇒ None }
            i filter (i ⇒ i >= 1 && i <= answers.size)
          }

          val answer = answers(answerIdx.flatten.head)
          val chosenAlternatives = q.answers(answer)

          println(s"Possible alternatives left:")
          chosenAlternatives map (a ⇒ s"  ${a.node}") foreach println

          if (chosenAlternatives.size <= 1) chosenAlternatives.headOption
          else loopUntilOneLeft(chosenAlternatives)
      }
    }

    loopUntilOneLeft(alternativeTrees)
  }

}
