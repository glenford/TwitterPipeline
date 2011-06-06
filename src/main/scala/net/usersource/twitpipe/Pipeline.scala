package net.usersource.twitpipe


import net.usersource.twitpipe.JSON._
import akka.actor.{ActorRef, Actor}
import akka.actor.Actor._
import sjson.json.JsonSerialization._
import dispatch.json.Js


class ConsoleDump extends Actor {
  def receive = {
    case s: Status => println(">>> " + s.user.screen_name + " : " + s.text)
  }
}

class ParseMessages( nextStatusStage: ActorRef ) extends Actor {
  def receive = {
    case s: String => {
      try {
        nextStatusStage ! fromjson[Status](Js(s))
      }
      catch {
        case _ => {} // delete, ignore for now
      }
    }
  }
}

object Pipeline {
  val sink = actorOf[ConsoleDump].start()
  val parser = actorOf(new ParseMessages(sink)).start()
  val sample = actorOf(new SampleIngest(new TwitterEndpoint,parser)).start()

  def start() = { sample ! Connect }
  def stop() = { sample ! CloseConnection }
}