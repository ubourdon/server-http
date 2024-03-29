import sbt._
import sbt.Keys._

object ServerHttpBuild extends Build {
    // make library => 'sbt + package' & 'sbt + make-pom'

    lazy val root = Project(id = "server-http", base = file("."),
        settings = Project.defaultSettings ++ Seq(
            name := "server-http",

            description := "server http nio & akka based",

            version := "0.1-SNAPSHOT",

            scalaVersion := "2.10.2",

            libraryDependencies ++= Seq(
                "com.typesafe.akka"         %% "akka-actor"         % "2.2.3"           ,
                "org.scalatest"             %%  "scalatest"         % "2.0"     % "test"
            )
        )
    )
}
