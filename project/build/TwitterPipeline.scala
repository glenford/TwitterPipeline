import sbt._
import reaktor.scct.ScctProject
import net.usersource.jettyembed.JettyEmbedWebProject

class TwitterPipeline(info: ProjectInfo) extends JettyEmbedWebProject(info) with AkkaProject with ScctProject with IdeaProject {

  val signpost = "oauth.signpost" % "signpost-core" % "1.2"
  val signpostHttpClient = "oauth.signpost" % "signpost-commonshttp4" % "1.2.1.1"
  val httpClient = "org.apache.httpcomponents" % "httpclient" % "4.1.1"

  val scalaTest = "org.scalatest" % "scalatest" % "1.3" % "test->default"
}
