import sbt._
import sbt.Keys._

object IatiDownloaderBuild extends Build {

  lazy val iatiDownloader = Project(
    id       = "iati-downloader",
    base     = file("."),
    settings = Project.defaultSettings ++ Seq(
      name                 := "IATI Downloader",
      organization         := "uk.gov.dfid",
      version              := "0.1-SNAPSHOT",
      scalaVersion         := "2.10.3",
      resolvers           ++= Seq(
        "spray"    at "http://repo.spray.io/",
        "typesafe" at "http://repo.typesafe.com/typesafe/releases/",
        "neo4j"    at "http://m2.neo4j.org/",
        "basex"    at "http://files.basex.org/maven",
        "xql"      at "http://xqj.net/maven"
      ),
      libraryDependencies ++= Seq(
        "org.apache.httpcomponents" %  "httpclient"                  % "4.2.5",
        "io.spray"                  %% "spray-json"                  % "1.2.5",
        "com.typesafe.akka"         %% "akka-actor"                  % "2.2.0-RC2",
        "org.mashupbots.socko"      %% "socko-webserver"             % "0.3.0",
        "nl.grons"                  %% "metrics-scala"               % "3.0.0",
        "org.neo4j"                 %  "neo4j-kernel"                % "1.9.1",
        "org.neo4j"                 %  "neo4j-cypher"                % "1.9.1",
        "org.neo4j"                 %  "neo4j-lucene-index"          % "1.9.1",
        "org.basex"                 %  "basex"                       % "7.6",
        "org.scalatest"             %% "scalatest"                   % "1.9.1" % "test",
        "org.scalamock"             %% "scalamock-scalatest-support" % "3.0.1" % "test"
      )
    )
  )
}
