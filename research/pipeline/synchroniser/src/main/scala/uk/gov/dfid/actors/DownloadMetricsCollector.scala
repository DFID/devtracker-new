package uk.gov.dfid.synchroniser.actors

import com.codahale.metrics.MetricRegistry
import akka.actor.Actor
import uk.gov.dfid.actors.Commands.{DownloadComplete, DownloadStarted, ResourceChanged}
import uk.gov.dfid.reporting.DownloadStatuses._
import uk.gov.dfid.synchroniser.support.Instrumented

class DownloadMetricsCollector extends Actor with Instrumented {

  private val states = Seq("new", "updated", "not_updated", "deleted", "bad_response", "error", "invalid").map("downloads." + _)

  def receive = {
    case DownloadStarted() => {
      resetDownloadMetrics
      metrics.counter("downloads.active").inc()
    }
    case ResourceChanged(summary) => summary.result match {
      case New            => metrics.counter("downloads.new").inc
      case Updated        => metrics.counter("downloads.updated").inc
      case NotUpdated     => metrics.counter("downloads.not_updated").inc
      case Deleted        => metrics.counter("downloads.deleted").inc
      case BadResponse(_) => metrics.counter("downloads.bad_response").inc
      case Error(_)       => metrics.counter("downloads.error").inc
      case Invalid        => metrics.counter("downloads.invalid").inc
    }
    case DownloadComplete(_) =>
      metrics.counter("downloads.active").dec()
  }

  def resetDownloadMetrics {
    states.foreach { key =>
      val name = MetricRegistry.name(classOf[DownloadMetricsCollector], key)
      metrics.registry.remove(name)
      metrics.counter(key)
    }
  }
}
