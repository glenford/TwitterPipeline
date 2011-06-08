package net.usersource.twitpipe

import org.apache.http.client.methods.HttpPost
import org.apache.http.params.HttpConnectionParams
import org.apache.http.impl.client.DefaultHttpClient
import java.io.{InputStreamReader, BufferedReader}


class TwitterEndpoint extends Endpoint with OAuth {
  def uri = "http://stream.twitter.com/1/statuses/sample.json"

  def connect:Either[Error,BufferedReader] = {
    try {
      val request = new HttpPost(uri)
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
