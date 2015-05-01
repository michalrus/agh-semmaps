package agh.semmaps

import java.io.File

import com.vividsolutions.jts.geom.{ Geometry, GeometryFactory }
import com.vividsolutions.jts.io.gml2.GMLReader

import scala.util.Try
import scala.xml.XML

final case class JmlObject(origin: File, geometry: Geometry, props: Map[String, String]) {
  def isAncestor(that: JmlObject): Boolean = this != that && (this.geometry covers that.geometry)
  def distance(that: JmlObject): Double = this.geometry distance that.geometry
}

final case class JmlTree(node: JmlObject, children: Set[JmlTree])

object JmlParser {

  def apply(input: File): Set[JmlObject] = {
    val features = XML.loadFile(input) \\ "feature"
    val parser = new GMLReader
    features.toSet map { (feature: xml.Node) ⇒
      val geom = parser.read((feature \ "geometry" flatMap (_.child)).mkString, new GeometryFactory())
      val props = feature \ "property" map (p ⇒ (p \@ "name", p.text.trim)) filter { case (k, v) ⇒ v.nonEmpty }
      JmlObject(input, geom, props.toMap)
    }
  }

}

object GmlParser {

  def apply(inputDirectory: File): Try[List[JmlTree]] = Try {
    require(inputDirectory.isDirectory, s"$inputDirectory: not a directory")
    val files = inputDirectory.listFiles.filter(_.isFile).filter(_.getName.toUpperCase.endsWith(".JML")).toSet
    TreeBuilder(files flatMap JmlParser.apply)
  }

}
