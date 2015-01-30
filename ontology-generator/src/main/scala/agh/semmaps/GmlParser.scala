package agh.semmaps

import java.io.File

import com.vividsolutions.jts.geom.{ Geometry, GeometryFactory }
import com.vividsolutions.jts.io.gml2.GMLReader

import scala.util.Try
import scala.xml.XML

sealed trait JmlType
case object JmlArea extends JmlType
case object JmlDoor extends JmlType
case object JmlObstacle extends JmlType
case object JmlPoi extends JmlType

final case class JmlObject(tpe: JmlType, geometry: Geometry, props: Map[String, String]) {
  def isAncestor(that: JmlObject): Boolean = this != that && (this.geometry covers that.geometry)
}

final case class JmlTree(node: JmlObject, children: List[JmlObject])

object JmlParser {

  def apply(tpe: JmlType, input: File): List[JmlObject] = {
    val features = XML.loadFile(input) \\ "feature"
    val parser = new GMLReader
    features.toList map { feature ⇒
      val geom = parser.read((feature \ "geometry" flatMap (_.child)).mkString, new GeometryFactory())
      val props = feature \ "property" map (p ⇒ (p \@ "name", p.text))
      JmlObject(tpe, geom, props.toMap)
    }
  }

}

object GmlParser {

  val Files = Map[String, JmlType]("Area.jml" → JmlArea, "Door.jml" → JmlDoor, "Obstacle.jml" → JmlObstacle, "POI.jml" → JmlPoi)

  def apply(inputDirectory: File): Try[Ontology] = Try {
    require(inputDirectory.isDirectory, s"$inputDirectory: not a directory")
    val files = inputDirectory.listFiles().toSet

    require(files.map(_.getName) == Files.keySet, s"$inputDirectory: should contain only ${Files.keySet}")

    TreeBuilder(files flatMap (f ⇒ JmlParser(Files(f.getName), f)))
  } flatMap Ontology.fromJmlTrees

}
