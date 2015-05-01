package agh.semmaps

import scala.util.{ Failure, Success }

object Main extends App {

  Config(args.toList) match {
    case Some(cnf) ⇒
      GmlParser(cnf.inputDirectory) match {
        case Success(jmlTrees) ⇒
          cnf.prologOntology foreach (o ⇒ PrologWriter(jmlTrees)(o) recover { case err ⇒ err.printStackTrace() })
        case Failure(err) ⇒
          err.printStackTrace()
      }
    case None ⇒
  }

}
