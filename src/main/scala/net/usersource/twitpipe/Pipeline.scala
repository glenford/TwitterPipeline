package net.usersource.twitpipe

import akka.actor.Actor._

import net.usersource.twitpipe.JSON._

import sjson.json.JsonSerialization._
import dispatch.json.Js
import akka.actor.{ActorRef, Actor}


class ConsoleDump extends Actor {
  def receive = {
    case s: Status => println(">>> " + s.user.screen_name + " : " + s.text)
  }
}

class ParseMessages( nextStage: ActorRef ) extends Actor {
  def receive = {
    case s: String => nextStage ! fromjson[Status](Js(s))
  }
}

object Pipeline {
  val sink = actorOf[ConsoleDump].start()
  val parser = actorOf(new ParseMessages(sink)).start()
  val sample = actorOf(new SampleIngest(new TwitterEndpoint,parser)).start()

  def start() = { sample ! Connect }
}