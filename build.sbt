name := "no-carrier"

version := "1.0.0"

scalaVersion := "2.11.8"

mainClass := Some("com.getbootstrap.no_carrier.Main")

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

val jcabiV = "0.24"

libraryDependencies += "com.jcabi" % "jcabi-github" % jcabiV

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"

libraryDependencies += "ch.qos.logback" %  "logback-classic" % "1.1.2"

libraryDependencies += "org.specs2" %% "specs2-core" % "3.8.4" % "test"

// Because jcabi-github:

libraryDependencies += "com.google.code.findbugs" % "annotations" % "2.0.1"

packageOptions in (Compile, packageBin) ++= {
  Seq(
    Package.ManifestAttributes("JCabi-Version" -> jcabiV),
    Package.ManifestAttributes("JCabi-Build" -> "abcdef"),
    Package.ManifestAttributes("JCabi-Date" -> "2015-08-01")
  )
}

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "–Xlint", "-encoding", "utf8")

scalacOptions in Test ++= Seq("-Yrangepos")

// parallelExecution in Test := false

Revolver.settings
