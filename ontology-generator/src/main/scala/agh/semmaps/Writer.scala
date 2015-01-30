package agh.semmaps

import java.io.File

import scala.util.Try

trait Writer {
  def apply(output: File)(ontology: Ontology): Try[Unit]
}

object PrologWriter extends Writer {
  def apply(output: File)(ontology: Ontology): Try[Unit] = Try {
    TreeBuilder.prettyPrint(ontology.trees)

    val DumpResult(dmp, classesUsed) = (ontology.trees foldLeft DumpResult("", Set.empty)) {
      case (acc, tree) ⇒
        dump(0)(tree) match {
          case Some(dr) ⇒ DumpResult(acc.dump + ".\n" + dr.dump, acc.classesUsed ++ dr.classesUsed)
          case None     ⇒ acc
        }
    }
    classesUsed foreach (cl ⇒ println(s"class($cl)."))
    println()
    println(dmp + ".")
  }
  final case class DumpResult(dump: String, classesUsed: Set[String])
  def dump(indent: Int)(tree: JmlTree): Option[DumpResult] = {
    val i = "  " * indent

    val children = (tree.children.toList map dump(indent + 1)).flatten

    val hasText = if (children.isEmpty) "" else " has [\n" + children.map(_.dump).mkString(",\n") + "\n" + i + "]"
    val chClass = children.flatMap(_.classesUsed)
    def props(m: Map[String, String]): String = m map { case (k, v) ⇒ s"""${k.toLowerCase}: "$v"""" } mkString ", "

    tree.node.tpe match {
      case JmlArea ⇒
        Some(DumpResult(i + "classroom{}" + hasText, Set("classroom") ++ chClass))

      case JmlObstacle | JmlPoi ⇒
        val kind = tree.node.props.getOrElse("Kind", "unnamed obstacle").replace(" ", "_").toLowerCase
        Some(DumpResult(i + s"$kind{${props(tree.node.props filterKeys (_ != "Kind"))}}" + hasText, Set(kind) ++ chClass))

      case JmlDoor ⇒
        val kind = "door"
        Some(DumpResult(i + s"$kind{${props(tree.node.props)}}" + hasText, Set(kind) ++ chClass))
    }
  }
}

object OwlWriter extends Writer {
  // TODO: really?…
  def apply(output: File)(ontology: Ontology): Try[Unit] = ???
}
