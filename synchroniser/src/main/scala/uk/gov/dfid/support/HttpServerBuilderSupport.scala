package uk.gov.dfid.support

import org.mashupbots.socko.events.SockoEvent
import akka.actor.ActorSystem
import org.mashupbots.socko.webserver.{WebServerConfig, WebServer}

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 10/07/2013
 * Time: 11:54
 * To change this template use File | Settings | File Templates.
 */
trait HttpServerBuilderSupport {

  def buildServer(routes: PartialFunction[SockoEvent, Unit], port: Int)(implicit system: ActorSystem) = {

    val server = new WebServer(WebServerConfig(port = port), routes, system)

    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run {
        server.stop()
      }
    })

    server
  }
}
