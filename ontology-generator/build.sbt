organization := "agh.semmaps"

name := "ontology-generator"

scalaVersion := "2.11.5"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint",
  "-Xfatal-warnings",
  "-Yno-adapted-args", "-Yrangepos", "-Ywarn-dead-code", "-Ywarn-inaccessible",
  "-Ywarn-infer-any", "-Ywarn-nullary-override", "-Ywarn-numeric-widen",
  "-Ywarn-unused", "-Ywarn-unused-import", "-Ywarn-value-discard")

resolvers += "michalrus.com repo" at "http://maven.michalrus.com/"

wartremoverErrors ++= Warts.allBut(Wart.Nothing, Wart.Any, Wart.NoNeedForMonad)

wartremoverExcluded += baseDirectory.value / "src" / "main" / "scala" / "agh" / "semmaps" / "Config.scala"

resolvers += Resolver.sonatypeRepo("public")

libraryDependencies ++= Seq(
  "com.github.scopt" %% "scopt" % "3.3.0",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.11.6" % "test"
)
