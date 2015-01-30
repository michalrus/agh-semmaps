package agh.semmaps

import java.io.File

import com.vividsolutions.jts.geom.{Geometry, GeometryFactory}
import com.vividsolutions.jts.io.gml2.GMLReader

import scala.util.Try
import scala.xml.XML

final case class JmlObject(geometry: Geometry, props: Map[String, String]) // :(

object JmlParser {

  def apply(input: File): List[JmlObject] = {
    val features = XML.loadFile(input) \\ "feature"
    val parser = new GMLReader
    features.toList map { feature ⇒
      val geom = parser.read((feature \ "geometry" flatMap (_.child)).mkString, new GeometryFactory())
      val props = feature \ "property" map (p ⇒ (p \@ "name", p.text))
      JmlObject(geom, props.toMap)
    }
  }

}

object GmlParser {

  val Processors = Map("Area.jml" → processArea _, "Door.jml" → processDoor _, "Obstacle.jml" → processObstacle _, "POI.jml" → processPoi _)

  def apply(inputDirectory: File): Try[Ontology] = Try {
    require(inputDirectory.isDirectory, s"$inputDirectory: not a directory")
    val files = inputDirectory.listFiles().toSet

    require(files.map(_.getName) == Processors.keySet, s"$inputDirectory: should contain only ${Processors.keySet}")

    files.foldLeft(Ontology()) {
      case (onto, file) ⇒
        val p = JmlParser(file)
        println(s"------------------------\n$file\n\n$p\n------------------------")
        Processors(file.getName)(onto, p)
    }
  }

  def processArea(input: Ontology, door: List[JmlObject]): Ontology = ???

  def processDoor(input: Ontology, door: List[JmlObject]): Ontology = ???

  def processObstacle(input: Ontology, door: List[JmlObject]): Ontology = ???

  def processPoi(input: Ontology, door: List[JmlObject]): Ontology = ???

}
