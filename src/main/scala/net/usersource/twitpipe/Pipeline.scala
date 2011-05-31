package net.usersource.twitpipe

import akka.actor.Actor
import akka.actor.Actor._


class ConsoleDump extends Actor {
  def receive = {
    case s: String => println(s)
  }
}

object Pipeline {
  val sink = actorOf[ConsoleDump].start()
  val sample = actorOf(new SampleIngest(new TwitterEndpoint,sink)).start()

  def start() = { sample ! Connect }
}