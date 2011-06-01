package net.usersource.twitpipe

import java.io.BufferedReader
import java.net.SocketTimeoutException
import akka.dispatch.Dispatchers
import akka.actor.{ActorRef, Actor}
import akka.event.EventHandler



case object Connect
case object NextMessage
case object CloseConnection

class SampleIngest( val endpoint: Endpoint, val sink: ActorRef ) extends Actor {

  self.dispatcher = Dispatchers.newThreadBasedDispatcher(self)

  var stream: Option[BufferedReader] = None

  private def connectSuccess(br: BufferedReader) = {
    EventHandler.info(this,"Connected")
    stream = Some(br)
    become(active)
    self ! NextMessage
  }

  private def connectFail(err: Error) = {
    EventHandler.error(this,"Connection Error : " + err.message)
    stream = None
  }

  private class ReadError( val message: String )

  private def readMessage: Either[ReadError,Option[String]] = {
    try {
      Right(Some(stream.get.readLine()))
    }
    catch {
      case e: SocketTimeoutException => Right(None)
      case e: Exception => Left(new ReadError(e.getMessage))
      case _ => Left(new ReadError("Unknown"))
    }
  }

  private def readFail(err: ReadError) = {
    self ! CloseConnection
  }

  private def readSuccess(message: Option[String]) = {
    message.map( sink ! _ )
    self ! NextMessage
  }

  protected def receive = inActive
  
  def inActive: Receive = {
    case Connect => {
      endpoint.connect fold (connectFail _, connectSuccess _)
    }
  }

  def active: Receive = {
    case NextMessage => {
      readMessage fold (readFail _, readSuccess _)
    }
    case CloseConnection => {
      EventHandler.warning(this,"CloseConnection")
      stream.get.close()
      stream = None
      become(inActive)
    }
  }

}