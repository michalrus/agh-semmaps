package agh.semmaps

import java.io.File

import scala.util.Try

trait Writer {
  def apply(output: File)(ontology: Ontology): Try[Unit]
}

object PrologWriter extends Writer {
  def apply(output: File)(ontology: Ontology): Try[Unit] = ???
}

object OwlWriter extends Writer {
  // TODO: really?…
  def apply(output: File)(ontology: Ontology): Try[Unit] = ???
}
