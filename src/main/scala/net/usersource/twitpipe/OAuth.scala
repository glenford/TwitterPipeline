package net.usersource.twitpipe

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer


trait OAuth {
  def consumerKey = System.getProperty("consumerKey")
  def consumerSecret = System.getProperty("consumerSecret")
  def accessToken = System.getProperty("accessToken")
  def accessSecret = System.getProperty("accessSecret")

  def consumer = {
    if( consumerKey == null || consumerKey.isEmpty ||
      consumerSecret == null || consumerKey.isEmpty ||
      accessToken == null || accessToken.isEmpty ||
      accessSecret == null || accessSecret.isEmpty )
      throw new RuntimeException("Ensure that consumerKeys, consumerSecret, accessToken, accessSecret are set")

    val _consumer = new CommonsHttpOAuthConsumer(consumerKey,consumerSecret)
    _consumer.setTokenWithSecret(accessToken,accessSecret)
    _consumer
  }
}