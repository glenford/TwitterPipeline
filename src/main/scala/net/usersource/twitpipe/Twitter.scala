package net.usersource.twitpipe

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.{HttpPost, HttpGet}
import java.io.{InputStreamReader, BufferedReader}
import org.apache.http.params.{HttpConnectionParams, HttpParams}

object Twitter {

  case class Error( val message: String )

  val sampleUri = "http://stream.twitter.com/1/statuses/sample.json"

  val connectionTimeout = 60 * 1000
  val soTimeout = 60 * 1000

  lazy val consumer = {
    val consumerKey = System.getProperty("consumerKey")
    val consumerSecret =  System.getProperty("consumerSecret")
    val accessToken = System.getProperty("accessToken")
    val accessSecret = System.getProperty("accessSecret")

    if( consumerKey == null || consumerKey.isEmpty ||
        consumerSecret == null || consumerKey.isEmpty ||
        accessToken == null || accessToken.isEmpty ||
        accessSecret == null || accessSecret.isEmpty )
      throw new RuntimeException("Ensure that consumerKeys, consumerSecret, accessToken, accessSecret are set")
    
    val _consumer = new CommonsHttpOAuthConsumer(consumerKey,consumerSecret)
    _consumer.setTokenWithSecret(accessToken,accessSecret)
    _consumer
  }

  
  def getSampleBufferReader: Either[Error,BufferedReader] = {
    try {
      val request = new HttpPost(sampleUri)
      HttpConnectionParams.setConnectionTimeout(request.getParams,connectionTimeout)
      HttpConnectionParams.setSoTimeout(request.getParams,soTimeout)

      consumer.sign(request);

      val httpClient = new DefaultHttpClient();
      val response = httpClient.execute(request);

      if( response.getStatusLine.getStatusCode == 200 ) {
        Right(new BufferedReader(new InputStreamReader(response.getEntity.getContent)))
      }
      else {
        Left(new Error( "HTTP: " + response.getStatusLine))
      }
    }
    catch {
      case e: Exception => Left(new Error( "Exception: " + e.getMessage ))
    }
  }
}







