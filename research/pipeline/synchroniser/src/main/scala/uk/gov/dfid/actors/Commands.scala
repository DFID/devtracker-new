package uk.gov.dfid.actors

import uk.gov.dfid.reporting.Summary
import org.mashupbots.socko.events.HttpRequestEvent


object Commands {

  // Coordinator commands
  case object Go
  case object Finish

  // Metrics Collector
  case object ResetMetrics

  // Synchroniser commands
  case object Synchronise
  case object Force

  // reporting commands
  case class Summarise(summary: List[Summary])
  case class Report(summary: Summary)

  //
  case class DownloadStarted()
  case class DownloadComplete(summary: List[Summary])
  case class ResourceChanged(summary: Summary)
}
