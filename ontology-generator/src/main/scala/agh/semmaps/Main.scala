package agh.semmaps

import scala.util.{ Failure, Success }

object Main extends App {

  Config(args.toList) flatMap (cnf ⇒ GmlParser(cnf.input) flatMap PrologWriter(cnf.output)) match {
    case Success(())  ⇒
    case Failure(err) ⇒ println(err); err.printStackTrace()
  }

}
