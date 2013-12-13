package uk.gov.dfid.actors

import akka.actor.Actor
import uk.gov.dfid.reporting.{Summary, ConsoleReporter}
import uk.gov.dfid.actors.Commands._

class Reporter extends Actor {

  import ConsoleReporter._

  def receive = {
    case DownloadComplete(summary) => summarise(summary)
    case ResourceChanged(summary)  => report(summary)
  }
}