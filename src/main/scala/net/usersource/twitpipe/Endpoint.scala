package net.usersource.twitpipe

import java.io.BufferedReader

trait Endpoint {
  def uri: String
  def connect: Either[Error,BufferedReader]
  def connectionTimeout = 60000
  def soTimeout = 1000
}