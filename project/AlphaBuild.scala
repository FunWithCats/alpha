import sbt._
import sbt.Keys._

object AlphaBuild extends Build {

  val useScalaVersion = "2.10.1"

  lazy val alpha = Project(
    id = "Alpha",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "Alpha",
      organization := "org.cc",
      version := "0.4.3",
      scalaVersion := useScalaVersion,
      autoScalaLibrary := false,
      scalacOptions += "-optimise",
      scalacOptions += "-Yinline-warnings",
      scalacOptions += "-feature",
      // add other settings here
      libraryDependencies += "org.scala-lang" % "scala-library" % useScalaVersion,
      libraryDependencies += "org.scala-lang" % "scala-swing"   % useScalaVersion,
      libraryDependencies += "org.scala-lang" % "scala-actors"  % useScalaVersion
    )
  )
}
