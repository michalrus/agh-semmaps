package agh.semmaps

import java.io.File

import com.vividsolutions.jts.geom.{ Geometry, GeometryFactory }
import com.vividsolutions.jts.io.gml2.GMLReader

import scala.util.Try
import scala.xml.XML

final case class JmlObject(origin: File, className: String, geometry: Geometry, props: Map[String, String]) {
  def isAncestor(that: JmlObject): Boolean = this != that && (this.geometry covers that.geometry)
  def distance(that: JmlObject): Double = this.geometry distance that.geometry
}

final case class JmlTree(node: JmlObject, children: Set[JmlTree]) {
  /** Finds all subtrees that match the given predicate */
  def find(p: JmlObject ⇒ Boolean): Set[JmlTree] =
    (if (p(node)) Set(this) else Set.empty) ++ (children flatMap (_.find(p)))
  override def toString: String = super.toString
}

object JmlParser {

  val SanitizeKeys = """[^a-z0-9_]""".r
  def sanitizeKeys(id: String): String = SanitizeKeys.replaceAllIn(id.toLowerCase, "_")

  def apply(classNameKey: String, splitKeys: Set[String], splitDelimiters: List[Char])(input: File): Set[JmlObject] = {
    val features = XML.loadFile(input) \\ "feature"
    val parser = new GMLReader
    features.toSet map { (feature: xml.Node) ⇒
      val geom = parser.read((feature \ "geometry" flatMap (_.child)).mkString, new GeometryFactory())
      val props = feature \ "property" map (p ⇒ (sanitizeKeys(p \@ "name"), p.text.trim)) filter { case (k, v) ⇒ v.nonEmpty }
      val splitted = props.toMap flatMap {
        case (k, v) if splitKeys contains k ⇒
          if (splitDelimiters.isEmpty) Map(sanitizeKeys(v) → "true")
          else v.split(splitDelimiters.toArray) map (_.trim) filter (_.nonEmpty) map (sanitizeKeys(_) → "true")
        case x ⇒ Map(x)
      }
      val className = JmlParser.sanitizeKeys(splitted.getOrElse(classNameKey, input.getName.dropRight(".jml".length)))
      JmlObject(input, className, geom, splitted)
    }
  }

}

object GmlParser {

  def apply(inputDirectory: File, classNameKey: String, splitKeys: Set[String], splitDelimiters: List[Char]): Try[List[JmlTree]] = Try {
    require(inputDirectory.isDirectory, s"$inputDirectory: not a directory")
    val files = inputDirectory.listFiles.filter(_.isFile).filter(_.getName.toUpperCase.endsWith(".JML")).toSet
    TreeBuilder(files flatMap JmlParser(classNameKey, splitKeys, splitDelimiters))
  }

}
