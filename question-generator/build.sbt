enablePlugins(JavaAppPackaging)

organization := "agh.semmaps"

name := "question-generator"

scalaVersion := "2.11.5"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint",
  "-Xfatal-warnings",
  "-Yno-adapted-args", "-Yrangepos", "-Ywarn-dead-code", "-Ywarn-inaccessible",
  "-Ywarn-infer-any", "-Ywarn-nullary-override", "-Ywarn-numeric-widen",
  "-Ywarn-unused", "-Ywarn-unused-import", "-Ywarn-value-discard")

resolvers += "michalrus.com repo" at "http://maven.michalrus.com/"

wartremoverErrors ++= Warts.allBut(Wart.Nothing, Wart.Any, Wart.NoNeedForMonad)

wartremoverExcluded ++= Seq(
  baseDirectory.value / "src" / "main" / "scala" / "agh" / "semmaps" / "Config.scala",
  baseDirectory.value / "src" / "main" / "scala" / "agh" / "semmaps" / "Main.scala",
  baseDirectory.value / "src" / "main" / "scala" / "agh" / "semmaps" / "TreeBuilder.scala"
)

resolvers += Resolver.sonatypeRepo("public")

libraryDependencies ++= Seq(
  "org.parboiled" %% "parboiled" % "2.1.0",
  "com.github.scopt" %% "scopt" % "3.3.0",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.3",
  "com.vividsolutions" % "jts" % "1.13",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.11.6" % "test"
)
