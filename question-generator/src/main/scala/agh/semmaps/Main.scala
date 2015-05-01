package agh.semmaps

import scala.util.{ Failure, Success }

object Main extends App {

  Config(args.toList) match {
    case Some(cnf) ⇒
      GmlParser(cnf.inputDirectory) match {
        case Success(jmlTrees) ⇒
          cnf.prologOntology foreach PrologWriter(jmlTrees)
        case Failure(err) ⇒
          err.printStackTrace()
      }
    case None ⇒
  }

}
