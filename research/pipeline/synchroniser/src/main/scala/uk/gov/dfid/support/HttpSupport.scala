package uk.gov.dfid.utils

import java.text.SimpleDateFormat
import org.apache.http.client.methods.{HttpUriRequest, HttpHead, HttpGet}
import org.apache.http.impl.client.DefaultHttpClient
import scala.io.Source
import org.apache.http.HttpResponse
import spray.json._
import spray.json.DefaultJsonProtocol._
import scala.util.{Success, Failure, Try}

trait HttpSupport {

  private val lastModifiedFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz")

  protected def get[T](url: String) = executeRequest(new HttpGet(url))

  protected def head[T](url: String) = executeRequest(new HttpHead(url))

  protected def executeRequest(request: HttpUriRequest) = Try(clientWithTimeout.execute(request))

  protected def escapeUrl(urlStr: String) = {
    val url = new java.net.URL(urlStr)
    val uri = new java.net.URI(url.getProtocol, url.getUserInfo, url.getHost, url.getPort, url.getPath, url.getQuery, url.getRef)
    uri.toString
  }

  protected def json(url: String) = {
    get(url).flatMap { response =>
      response.getStatusLine.getStatusCode match {
        case 200 => {
          val content = response.getEntity.getContent
          Success(Source.fromInputStream(content).mkString("").asJson)
        }
        case _ => Failure(new Exception("Unable to get resource"))
      }
    }
  }

  protected def urlLastModified(response: HttpResponse) = {
    response.getHeaders("Last-Modified").headOption.map { header =>
      lastModifiedFormat.parse(header.getValue).getTime
    }
  }

  private def clientWithTimeout = {
    val client = new DefaultHttpClient
    client.getParams.setParameter("http.socket.timeout", 10000)
    client.getParams.setParameter("http.connection.timeout", 10000)
    client.getParams.setParameter("http.connection-manager.timeout", 10000L)
    client.getParams.setParameter("http.protocol.head-body-timeout", 10000)
    client
  }
}