package net.usersource.twitpipe

import akka.actor.Actor
import java.io.BufferedReader
import net.usersource.twitpipe.Twitter.Error
import scala.Some
import java.net.SocketTimeoutException



case object Connect
case object NextMessage
case object CloseConnection

class SampleIngest extends Actor {

  var stream: Option[BufferedReader] = None

  private def connectSuccess(br: BufferedReader) = {
    stream = Some(br)
  }

  private def connectFail(err: Error) = {
    stream = None
  }

  class ReadError( val message: String )

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
    println(message)
    self ! NextMessage
  }

  protected def receive = {
    case Connect => {
      Twitter.getSampleBufferReader fold (connectFail _, connectSuccess _)
    }
    case NextMessage => {
      readMessage fold (readFail _, readSuccess _)
    }
    case CloseConnection => {
      stream.get.close
    }
  }
}