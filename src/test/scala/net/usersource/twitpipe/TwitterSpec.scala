package net.usersource.twitpipe

import org.scalatest.matchers.MustMatchers
import org.scalatest.{FeatureSpec, GivenWhenThen}
import org.scalatest.mock.MockitoSugar
import akka.actor.Actor._
import akka.testkit.TestKit
import akka.actor.ActorRef


class TwitterSpec extends FeatureSpec with GivenWhenThen with MustMatchers with MockitoSugar with TestKit {

  feature("Creating a Sample connector") {
    scenario("") {
      given("I have a mocked Twitter endpoint")
      val endpoint = mock[TwitterEndpoint]

      when("I create a Sample connector")
      val connector = actorOf( new SampleIngest(endpoint, this.testActor)).start

      and("I send it a connect message")
      connector ! Connect

      then("It shall send me some data via the queue")
      expectMsgClass(classOf[String])
    }
  }
}

