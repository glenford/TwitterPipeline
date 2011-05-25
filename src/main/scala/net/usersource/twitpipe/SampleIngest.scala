package net.usersource.twitpipe

import java.io.BufferedReader
import net.usersource.twitpipe.Twitter.Error
import java.net.SocketTimeoutException
import akka.dispatch.Dispatchers
import akka.actor.{ActorRef, Actor}
import akka.event.EventHandler


case object Connect
case object NextMessage
case object CloseConnection

class SampleIngest( val sink: ActorRef ) extends Actor {

  self.dispatcher = Dispatchers.newThreadBasedDispatcher(self)

  var stream: Option[BufferedReader] = None

  private def connectSuccess(br: BufferedReader) = {
    stream = Some(br)
    become(active)
  }

  private def connectFail(err: Error) = {
    EventHandler.error(this,"Connection Error : " + err.message)
    stream = None
  }

  private class ReadError( val message: String )

  private def readMessage: Either[ReadError,String] = {
    try {
      Right(stream.get.readLine())
    }
    catch {
      case e: SocketTimeoutException => Left(new ReadError("Socket Timeout"))
      case e: Exception => Left(new ReadError(e.getMessage))
      case _ => Left(new ReadError("Unknown"))
    }
  }

  private def readFail(err: ReadError) = {
    self ! CloseConnection
  }

  private def readSuccess(message: String) = {
    sink ! message
    self ! NextMessage
  }

  protected def receive = inActive
  
  def inActive: Receive = {
    case Connect => {
      Twitter.getSampleBufferReader fold (connectFail _, connectSuccess _)
    }
  }

  def active: Receive = {
    case NextMessage => {
      readMessage fold (readFail _, readSuccess _)
    }
    case CloseConnection => {
      stream.get.close
      stream = None
      become(inActive)
    }
  }

}