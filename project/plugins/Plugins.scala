
import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val jettyEmbeddedWarRepo = "Embeded Jetty Repo" at "https://github.com/glenford/repo/raw/master"
  val jettyEmbeddedWar = "net.usersource" % "sbt-jetty-embed-plugin" % "0.6.1"

  val sbtIdeaRepo = "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
  val sbtIdea = "com.github.mpeltonen" % "sbt-idea-plugin" % "0.3-SNAPSHOT"

  val scctRepo = "scct-repo" at "http://mtkopone.github.com/scct/maven-repo/"
  lazy val scctPlugin = "reaktor" % "scct-sbt-for-2.9" % "0.1-SNAPSHOT"


  val akkaRepo = "Akka Repo" at "http://akka.io/repository"
  val akkaPlugin = "se.scalablesolutions.akka" % "akka-sbt-plugin" % "1.1"
}
