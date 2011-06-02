package net.usersource.twitpipe

import org.scalatest.matchers.MustMatchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock
import akka.actor.Actor
import akka.actor.Actor._
import akka.testkit.TestKit
import akka.event.EventHandler
import akka.util.duration._
import java.io.BufferedReader
import java.net.SocketTimeoutException
import java.util.concurrent.{TimeUnit, LinkedBlockingQueue}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FeatureSpec, GivenWhenThen}

class TwitterSpec extends FeatureSpec with GivenWhenThen with MustMatchers with BeforeAndAfterEach with MockitoSugar with TestKit {

  val eventQueue = new LinkedBlockingQueue[Any]()
  val evtHandler = actorOf(new Actor() {
        self.dispatcher = EventHandler.EventHandlerDispatcher
        protected def receive = {
           case genericEvent => eventQueue.offer(genericEvent)
        }
      })
  EventHandler.addListener(evtHandler)

  override def beforeEach() {
    eventQueue.clear()
  }

  feature("Creating a Sample Ingest") {
    
    scenario("Creating a connection") {
      given("I have a mocked Twitter endpoint")
      val br = mock[BufferedReader]
      Mockito.when(br.readLine()).thenReturn("some data").thenAnswer( new Answer[Unit] {
        def answer(p1: InvocationOnMock) { Thread.sleep(100); throw new SocketTimeoutException() }
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

      and("wait till actor has had a chance to process nextevent")
      Thread.sleep(150)
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

      then("it shall result in an error event")
      eventQueue.poll(1000,TimeUnit.MILLISECONDS) match {
        case e: EventHandler.Error => {}
        case a: Any => fail("Failed to get Error, got [" + a + "]")
      }
    }

    scenario("Connects but dies after a few messages") {
      given("I have a mocked Twitter endpoint")
      val br = mock[BufferedReader]
      Mockito.when(br.readLine()).
        thenReturn("some data").
        thenReturn("some data").
        thenReturn("some data").
        thenAnswer( new Answer[Unit] {
          def answer(p1: InvocationOnMock) { Thread.sleep(100); throw new Exception() }
        } )
      val endpoint = mock[TwitterEndpoint]
      Mockito.when(endpoint.connect).thenReturn(Right(br))

      when("I create a Sample connector")
      val connector = actorOf( new SampleIngest(endpoint, this.testActor)).start

      and("I send it a connect message")
      connector ! Connect

      then("It shall send me some data")
      expectMsgClass(5 seconds,classOf[String])
      expectMsgClass(5 seconds,classOf[String])
      expectMsgClass(5 seconds,classOf[String])

      and("then we see an info followed by a warning event")
      eventQueue.poll(1000,TimeUnit.MILLISECONDS) match {
        case e: EventHandler.Info => {}
        case a: Any => fail("Failed to get Info, got [" + a + "]")
      }
      eventQueue.poll(1000,TimeUnit.MILLISECONDS) match {
        case e: EventHandler.Warning => {}
        case a: Any => fail("Failed to get Warning, got [" + a + "]")
      }

      and("and see the stream is closed")
      Mockito.verify(br).close()
    }

  }
}
