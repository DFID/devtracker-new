package uk.gov.dfid

import akka.actor._
import scala.concurrent.duration._
import uk.gov.dfid.actors.Commands._
import uk.gov.dfid.actors._
import uk.gov.dfid.synchroniser.actors.DownloadMetricsCollector
import com.codahale.metrics.MetricRegistry
import org.basex.BaseXServer
import org.mashupbots.socko.webserver.{WebServerConfig, WebServer}
import org.mashupbots.socko.routes.{Path, HttpRequest, Routes}
import uk.gov.dfid.synchroniser.actors.http._
import uk.gov.dfid.actors.Commands.ResourceChanged
import uk.gov.dfid.synchroniser.actors.http.IndexRequest
import uk.gov.dfid.actors.Commands.DownloadStarted
import uk.gov.dfid.actors.Commands.DownloadComplete
import uk.gov.dfid.synchroniser.actors.http.MetricsRequest
import uk.gov.dfid.synchroniser.actors.http.ForceRequest
import org.mashupbots.socko.events.HttpResponseStatus

object Application extends App {

  // create the overarching actor system that will manage our application
  implicit val system = akka.actor.ActorSystem("devtracker")

  // start basex TCP server
  val server = new BaseXServer

  // import our execution context
  import system.dispatcher

  // create central metric registry
  val metrics = new MetricRegistry

  // grab references to our system actors
  val coordinator = system.actorOf(Props[Coordinator]              , name="iati-registry-sync")
  val reporter    = system.actorOf(Props[Reporter]                 , name="console-reporter")
  val indexer     = system.actorOf(Props[Indexer]                  , name="xml-indexer")
  val collector   = system.actorOf(Props[DownloadMetricsCollector] , name="metrics-collector")

  // susbscribe to system wide events
  system.eventStream.subscribe(indexer   , classOf[ResourceChanged])
  system.eventStream.subscribe(indexer   , classOf[DownloadComplete])
  system.eventStream.subscribe(reporter  , classOf[ResourceChanged])
  system.eventStream.subscribe(reporter  , classOf[DownloadComplete])
  system.eventStream.subscribe(collector , classOf[DownloadStarted])
  system.eventStream.subscribe(collector , classOf[ResourceChanged])
  system.eventStream.subscribe(collector , classOf[DownloadComplete])

  // set up a scheduler to sync the registry
  system.scheduler.schedule(0 millis, 3 hours) {
    coordinator ! Go
  }

  // start the web servers
  val routes = Routes({
    case HttpRequest(request) => request match {
      case Path("/")        => system.actorOf(Props[AdminHandler]) ! IndexRequest(request)
      case Path("/metrics") => system.actorOf(Props[AdminHandler]) ! MetricsRequest(request, metrics)
      case Path("/force")   => system.actorOf(Props[AdminHandler]) ! ForceRequest(request)
      case Path("/health")  => system.actorOf(Props[AdminHandler]) ! HealthRequest(request)
      case _                => request.response.write(HttpResponseStatus.NOT_FOUND)
    }
  })

  val adminServer = new WebServer(WebServerConfig(port = 8890), routes, system)

  adminServer.start()

  Runtime.getRuntime.addShutdownHook(new Thread {
    override def run {
      server.stop()
      adminServer.stop()
    }
  })
}