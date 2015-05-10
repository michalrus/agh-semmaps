package agh.semmaps

import java.io.{ PrintWriter, File }

import scala.collection.immutable.Set
import scala.util.Try

object PrologWriter {
  def apply(trees: List[JmlTree], hideKeys: Set[String])(output: File): Try[Unit] = Try {
    val DumpResult(dmp, classesUsed) = (trees foldLeft DumpResult("", Set.empty)) {
      case (acc, tree) ⇒
        dump(0, hideKeys)(tree) match {
          case Some(dr) ⇒ DumpResult(acc.dump + (if (acc.dump.nonEmpty) ".\n" else "") + dr.dump, acc.classesUsed ++ dr.classesUsed)
          case None     ⇒ acc
        }
    }
    val result =
      s"""relation(has).
         |relation(consists_of).
         |relation(exists).
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
  def dump(indent: Int, hideKeys: Set[String])(tree: JmlTree): Option[DumpResult] = {
    val i = "  " * indent

    val children = (tree.children.toList map dump(indent + 1, hideKeys)).flatten

    def redundant(xs: List[String]): List[String] = {
      (xs groupBy identity mapValues (_.size) map { case (x, num) ⇒ x + (if (num > 1) s" * $num" else "") }).toList
    }
    val hasText = if (children.isEmpty) " exists" else " has [\n" + redundant(children.map(_.dump).sorted).mkString(",\n") + "\n" + i + "]"
    val chClass = children.flatMap(_.classesUsed)

    def props(node: JmlObject): String =
      node.props filterKeys (k ⇒ !hideKeys.contains(k)) map { case (k, v) ⇒ s"""$k: "$v"""" } mkString ", "

    Some(DumpResult(i + s"${tree.node.className}{${props(tree.node)}}" + hasText, Set(tree.node.className) ++ chClass))
  }
  /* FIXME: how do we output distances without UUIDs?
  def distances(trees: List[JmlTree]): String = {
    val all = trees.toSet flatMap nodeSet
    all.subsets(2) map {
      _.toList match {
        case a :: b :: Nil ⇒
          s"""distance("${a.uuid}", "${b.uuid}", "${a distance b}")."""
        case _ ⇒ ""
      }
    } mkString "\n"
  }
  */
}
