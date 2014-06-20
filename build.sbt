name := "macroid-protoui"

description := "A Scala GUI DSL for Android"

homepage := Some(url("http://github.com/macroid/macroid"))

organization := "org.macroid"

version := "2.0.0-SNAPSHOT"

scalaVersion := "2.11.1"

scalacOptions ++= Seq("-feature", "-deprecation")

crossScalaVersions := Seq("2.10.4", "2.11.1")

scalacOptions in (Compile, doc) ++= Seq(
  "-sourcepath", baseDirectory.value.getAbsolutePath,
  "-doc-source-url", "https://github.com/macroid/macroid/tree/master€{FILE_PATH}.scala"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Typesafe" at "http://repo.typesafe.com/typesafe/releases/",
  "Android" at (file(System.getenv("ANDROID_SDK_HOME")) / "extras" / "android" / "m2repository").getCanonicalFile.toURI.toString
)

autoCompilerPlugins := true

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  compilerPlugin("org.scalamacros" % "paradise" % "2.0.0" cross CrossVersion.full)
)

libraryDependencies ++= (CrossVersion.partialVersion(scalaVersion.value) match {
  case Some((2, 10)) ⇒
    Seq("org.scalamacros" %% "quasiquotes" % "2.0.0")
  case _ ⇒
    Seq()
})

libraryDependencies ++= Seq(
  "org.macroid" %% "macroid" % "2.0.0-M2",
  "com.google.android" % "android" % "4.1.1.4" % "provided",
  "com.android.support" % "support-v13" % "19.0.0",
  "org.scala-lang.modules" %% "scala-async" % "0.9.1",
  "org.brianmckenna" %% "wartremover" % "0.10",
  "org.scalatest" %% "scalatest" % "2.1.5" % "test"
)

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
