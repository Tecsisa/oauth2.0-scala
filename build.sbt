name := "oauth2.0-scala"

scalaVersion in ThisBuild := "2.11.8"

organization in ThisBuild := "com.algd"

version in ThisBuild := "0.2.3"

scalacOptions in ThisBuild := Seq("-unchecked", "-deprecation", "-target:jvm-1.8", "-encoding", "utf8", "-feature")

lazy val root = (project in file("."))
  .aggregate(
    `oauth2-scala-core`,
    `oauth2-scala-akka-http`)
  .settings(
    publishArtifact := false
  )

lazy val `oauth2-scala-core` = project
  .settings(
    libraryDependencies ++= {
      Seq(
        "com.github.nscala-time" %% "nscala-time" % "2.0.0",
        "org.scalatest"     %% "scalatest" % "3.0.0-RC1" % "test"
      )
    }
  )

lazy val `oauth2-scala-akka-http` = project
  .settings(
    mainClass in Compile := None,
    libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % "2.4.4"
  ).dependsOn(`oauth2-scala-core`)