package agh.semmaps

import java.io.File

import scala.util.Try

final case class Config(input: File, output: File)

object Config {

  def apply(args: List[String]): Try[Config] = ???

}
