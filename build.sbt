lazy val circeVersion = "0.14.2"
lazy val circeFs2Version = "0.14.0"
lazy val fs2Version = "3.2.9"
lazy val fs2DataVersion = "1.4.1"
lazy val pPrintVersion = "0.7.3"
lazy val scalaGraphVersion = "1.13.5"
lazy val catsEffectTestingScalaTest = "1.4.0"

lazy val root = (project in file("."))
  .settings(
    name := "traffic",
    scalaVersion := "2.13.8",
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-io" % fs2Version,
      "com.lihaoyi" %% "pprint" % pPrintVersion,
      "io.circe" %% "circe-fs2" % circeFs2Version,
      "io.circe" %% "circe-generic" % circeVersion,
      "org.gnieh" %% "fs2-data-json" % fs2DataVersion,
      "org.gnieh" %% "fs2-data-json-circe" % fs2DataVersion,
      "org.scala-graph" %% "graph-core" % scalaGraphVersion,
      "org.typelevel" %% "cats-effect-testing-scalatest" % catsEffectTestingScalaTest % Test
    )
  )
