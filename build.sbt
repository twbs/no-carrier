name := "no-carrier"

version := "1.0"

scalaVersion := "2.11.5"

mainClass := Some("com.getbootstrap.no_carrier.Main")

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

libraryDependencies += "com.jcabi" % "jcabi-github" % "0.21.3"

libraryDependencies += "org.specs2" %% "specs2" % "2.3.12" % "test"

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "–Xlint", "-encoding", "utf8")

scalacOptions in Test ++= Seq("-Yrangepos")

// parallelExecution in Test := false

Revolver.settings
