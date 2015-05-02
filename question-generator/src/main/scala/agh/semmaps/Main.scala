package agh.semmaps

import scala.util.{ Failure, Success }

object Main extends App {

  Config(args.toList) match {
    case Some(cnf) ⇒
      GmlParser(cnf.inputDirectory, cnf.splitKeys, cnf.splitDelimiters) match {
        case Success(jmlTrees) ⇒
          cnf.prologOntology zip cnf.prologKey foreach { case (o, key) ⇒ PrologWriter(jmlTrees, key)(o) recover { case err ⇒ err.printStackTrace() } }
        case Failure(err) ⇒
          err.printStackTrace()
      }
    case None ⇒
  }

}
