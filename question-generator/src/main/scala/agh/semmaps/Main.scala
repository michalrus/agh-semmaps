package agh.semmaps

object Main extends App {

  def log(s: String) = Console.err.println(s)

  Config(args.toList) foreach { cnf ⇒
    try {
      log("parsing GML...")
      val jmlTrees = GmlParser(cnf.inputDirectory, cnf.classNameKey, cnf.splitKeys, cnf.splitDelimiters).get
      log("done parsing")

      // write the prolog ontology if necessary
      cnf.prologOntology foreach { o ⇒ PrologWriter(jmlTrees, Set(cnf.classNameKey))(o).get }

      // select only the alternatives
      val alternativeTrees = cnf.alternatives flatMap { case (k, v) ⇒ jmlTrees flatMap (_.find(_.props.get(k).contains(v))) }

      log(s"${alternativeTrees.size} alternative(s) selected")
      alternativeTrees foreach (t ⇒ log(s"  ${t.node}"))

      QuestionGenerator(alternativeTrees)
    }
    catch {
      case e: Exception ⇒ Console.err.println(e.getMessage)
    }
  }

}
