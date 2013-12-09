name := "My Spray Example"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-feature")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "spray repo" at "http://repo.spray.io"

val akka = "2.2.3"
val spray = "1.2.0"

libraryDependencies ++=
    "ch.qos.logback" % "logback-classic" % "1.0.0" % "runtime" ::
    "com.typesafe.akka" %% "akka-actor" % akka ::
    "com.typesafe.akka" %% "akka-slf4j" % akka ::
    "com.typesafe.akka" %% "akka-testkit" % akka % "test" ::
    "io.spray" % "spray-caching" % spray ::
    "io.spray" % "spray-can" % spray ::
    "io.spray" % "spray-routing" % spray ::
    "io.spray" % "spray-testkit" % spray % "test" ::
    "io.spray" %% "spray-json" % "1.2.5" ::
    "org.scalatest" %% "scalatest" % "2.0" % "test" ::
    Nil

scalariformSettings

seq(Revolver.settings: _*)
