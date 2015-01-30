package agh.semmaps

import java.io.{ PrintWriter, File }

import scala.util.Try

trait Writer {
  def apply(output: File)(ontology: Ontology): Try[Unit]
}

object PrologWriter extends Writer {
  def apply(output: File)(ontology: Ontology): Try[Unit] = Try {
    val DumpResult(dmp, classesUsed) = (ontology.trees foldLeft DumpResult("", Set.empty)) {
      case (acc, tree) ⇒
        dump(0)(tree) match {
          case Some(dr) ⇒ DumpResult(acc.dump + (if (acc.dump.nonEmpty) ".\n" else "") + dr.dump, acc.classesUsed ++ dr.classesUsed)
          case None     ⇒ acc
        }
    }
    val result =
      s"""relation(has).
         |relation(consists_of).
         |
         |${classesUsed map (cl ⇒ s"class($cl).") mkString "\n"}
         |
         |$dmp.""".stripMargin
    if (output.toString == "-") println(result) else {
      val pw = new PrintWriter(output)
      pw.write(result)
      pw.close()
    }
  }
  final case class DumpResult(dump: String, classesUsed: Set[String])
  def dump(indent: Int)(tree: JmlTree): Option[DumpResult] = {
    val i = "  " * indent

    val children = (tree.children.toList map dump(indent + 1)).flatten

    def redundant(xs: List[String]): List[String] = {
      (xs groupBy identity mapValues (_.size) map { case (x, num) ⇒ x + (if (num > 1) s" * $num" else "") }).toList
    }
    val hasText = if (children.isEmpty) "" else " has [\n" + redundant(children.map(_.dump).sorted).mkString(",\n") + "\n" + i + "]"
    val chClass = children.flatMap(_.classesUsed)
    def props(m: Map[String, String]): String = m map { case (k, v) ⇒ s"""${k.toLowerCase}: "$v"""" } mkString ", "

    tree.node.tpe match {
      case JmlArea | JmlObstacle | JmlPoi ⇒
        val kind = tree.node.props.getOrElse("Kind", "unnamed").replace(" ", "_").toLowerCase
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
