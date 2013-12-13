package uk.gov.dfid.synchroniser.actors.http

import akka.actor.Actor
import org.mashupbots.socko.events.HttpRequestEvent
import com.codahale.metrics.MetricRegistry
import uk.gov.dfid.implicits.SuperHttpResponseMessage
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.duration._
import uk.gov.dfid.actors.Commands.Go
import uk.gov.dfid.json.MetricsProtocol._
import org.basex.BaseXServer
import spray.json.{JsObject, JsBoolean}

case class IndexRequest(request: HttpRequestEvent)
case class ForceRequest(request: HttpRequestEvent)
case class MetricsRequest(request: HttpRequestEvent, registry: MetricRegistry)
case class HealthRequest(request: HttpRequestEvent)

class AdminHandler extends Actor {

  import context.dispatcher

  def receive = {
    case msg: IndexRequest   => renderIndex(msg)
    case msg: MetricsRequest => renderMetrics(msg)
    case msg: ForceRequest   => forceSync(msg)
    case msg: HealthRequest  => checkHealth(msg)
  }

  def renderIndex(msg: IndexRequest) = {
    msg.request.response.write("""
      <html>
        <body>
          <h1>IATI Sychroniser</h1>
          <ul>
            <li>
              <a href="/metrics">Metrics</a>
            </li>
            <li>
              <a href="/force">Force Download</a>
            </li>
            <li>
              <a href="/health">Healthchecks</a>
            </li>
          </ul>
        </body>
      </html>""", "text/html")
    context.stop(self)
  }

  def renderMetrics(msg: MetricsRequest) {
    msg.request.response.json(msg.registry)
    context.stop(self)
  }

  def forceSync(msg: ForceRequest) {
    implicit val timeout = Timeout(5 seconds)
    val coordinator = context.actorSelection("akka://devtracker/user/iati-registry-sync")
    (coordinator ? Go).mapTo[Boolean].map { success =>
      msg.request.response.write(success.toString, "text/plain")
      context.stop(self)
    }
  }

  def checkHealth(msg: HealthRequest) = {
    val isBaseXServerRunning = BaseXServer.ping("localhost", 1984)
    msg.request.response.json(JsObject(
      "basex_running" -> JsBoolean(isBaseXServerRunning)
    ))
    context.stop(self)
  }

}
