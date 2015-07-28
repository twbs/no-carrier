name := "no-carrier"

version := "1.0"

scalaVersion := "2.11.5"

mainClass := Some("com.getbootstrap.no_carrier.Main")

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

libraryDependencies += "com.jcabi" % "jcabi-github" % "0.24"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"

libraryDependencies += "ch.qos.logback" %  "logback-classic" % "1.1.2"

libraryDependencies += "org.specs2" %% "specs2" % "2.3.12" % "test"

// Because jcabi-github:

libraryDependencies += "com.google.code.findbugs" % "annotations" % "2.0.1"

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "–Xlint", "-encoding", "utf8")

scalacOptions in Test ++= Seq("-Yrangepos")

// parallelExecution in Test := false

Revolver.settings
