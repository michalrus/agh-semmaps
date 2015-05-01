package agh.semmaps

import java.io.{ PrintWriter, File }

import scala.collection.immutable.Set
import scala.util.Try

trait Writer {
  def apply(trees: List[JmlTree])(output: File): Try[Unit]
  def nodeSet(tree: JmlTree): Set[JmlObject] =
    Set(tree.node) ++ (tree.children flatMap nodeSet)
}

object PrologWriter extends Writer {
  def apply(trees: List[JmlTree])(output: File): Try[Unit] = Try {
    val DumpResult(dmp, classesUsed) = (trees foldLeft DumpResult("", Set.empty)) {
      case (acc, tree) ⇒
        dump(0)(tree) match {
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
  val Prologize = """[^a-z0-9_]""".r
  def prologize(id: String) = Prologize.replaceAllIn(id.toLowerCase, "_")

  final case class DumpResult(dump: String, classesUsed: Set[String])
  def dump(indent: Int)(tree: JmlTree): Option[DumpResult] = {
    val i = "  " * indent

    val children = (tree.children.toList map dump(indent + 1)).flatten

    def redundant(xs: List[String]): List[String] = {
      (xs groupBy identity mapValues (_.size) map { case (x, num) ⇒ x + (if (num > 1) s" * $num" else "") }).toList
    }
    val hasText = if (children.isEmpty) " exists" else " has [\n" + redundant(children.map(_.dump).sorted).mkString(",\n") + "\n" + i + "]"
    val chClass = children.flatMap(_.classesUsed)

    def props(node: JmlObject): String = {
      val ps = node.props filterKeys (_ != "Kind") flatMap {
        case (k, v) if k.toUpperCase == "FEATURES" ⇒ v.split(';') map (_.trim) filter (_.nonEmpty) map (prologize(_) → "true")
        case x                                     ⇒ Map(x)
      }
      ps map { case (k, v) ⇒ s"""${k.toLowerCase}: "$v"""" } mkString ", "
    }

    val kind = prologize(tree.node.props.getOrElse("Kind", tree.node.origin.getName.dropRight(".jml".length)))

    Some(DumpResult(i + s"$kind{${props(tree.node)}}" + hasText, Set(kind) ++ chClass))
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
