package net.usersource.twitpipe

import org.scalatest.matchers.MustMatchers
import org.scalatest.{FeatureSpec, GivenWhenThen}
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito
import akka.actor.Actor._
import akka.testkit.TestKit
import java.io.BufferedReader
import akka.util.duration._


class TwitterSpec extends FeatureSpec with GivenWhenThen with MustMatchers with MockitoSugar with TestKit {

  feature("Creating a Sample connector") {
    scenario("") {
      given("I have a mocked Twitter endpoint")
      val br = mock[BufferedReader]
      Mockito.when(br.readLine()).thenReturn("some data")
      val endpoint = mock[TwitterEndpoint]
      Mockito.when(endpoint.connect).thenReturn(Right(br))

      when("I create a Sample connector")
      val connector = actorOf( new SampleIngest(endpoint, this.testActor)).start

      and("I send it a connect message")
      connector ! Connect

      then("It shall send me some data")
      expectMsgClass(5.seconds,classOf[String])
    }
  }
}

