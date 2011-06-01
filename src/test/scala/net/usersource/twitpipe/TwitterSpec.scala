package net.usersource.twitpipe

import org.scalatest.matchers.MustMatchers
import org.scalatest.{FeatureSpec, GivenWhenThen}
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito
import akka.actor.Actor._
import akka.testkit.TestKit
import java.io.BufferedReader
import akka.util.duration._
import scala.{PartialFunction, Left}
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock
import java.net.SocketTimeoutException

class TwitterSpec extends FeatureSpec with GivenWhenThen with MustMatchers with MockitoSugar with TestKit {

  feature("Creating a Sample Ingest") {
    
    scenario("Creating a connection") {
      given("I have a mocked Twitter endpoint")
      val br = mock[BufferedReader]
      Mockito.when(br.readLine()).thenReturn("some data").thenAnswer( new Answer[Unit] {
        def answer(p1: InvocationOnMock) { throw new SocketTimeoutException() }
      } )
      val endpoint = mock[TwitterEndpoint]
      Mockito.when(endpoint.connect).thenReturn(Right(br))

      when("I create a Sample connector")
      val connector = actorOf( new SampleIngest(endpoint, this.testActor)).start

      and("I send it a connect message")
      connector ! Connect

      then("It shall send me some data")
      expectMsgClass(5 seconds,classOf[String])

      and("we close the connection")
      connector ! CloseConnection
      Mockito.verify(br).close()
    }


    scenario("Failing to connect") {
      given("I have a mocked Twitter endpoint")
      val endpoint = mock[TwitterEndpoint]
      Mockito.when(endpoint.connect).thenReturn(Left(new Error("Connection Failed")))

      when("I create a Sample connector")
      val connector = actorOf( new SampleIngest(endpoint, this.testActor)).start

      and("I send it a connect message")
      connector ! Connect

      then("it shall not send me any data")
      expectNoMsg(5 seconds)
    }

  }
}

