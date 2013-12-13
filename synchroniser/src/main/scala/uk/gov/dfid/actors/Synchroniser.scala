package uk.gov.dfid.actors

import Commands._
import akka.actor.Actor
import uk.gov.dfid.implicits._
import uk.gov.dfid.reporting.Summary
import uk.gov.dfid.actors.Commands.ResourceChanged
import uk.gov.dfid.synchroniser.Downloader
import uk.gov.dfid.actors.Commands.DownloadComplete
import uk.gov.dfid.synchroniser.support.Instrumented
import uk.gov.dfid.support.IATIValidationSupport

class Synchroniser extends Actor {


  def receive = {
    case Synchronise => {
      val path = context.system.settings.config.tryGetString("synchroniser.path")

      val downloader = Downloader()
        .withCompletedHandler(handleComplete)
        .withResourceChangedHandler(handleResourceChange)

      handleStart

      path.map(downloader.to(_))
        .getOrElse(downloader)
        .execute

      sender ! Finish
    }
  }

  def handleStart {
    context.system.eventStream.publish(DownloadStarted())
  }

  def handleComplete(summary: List[Summary]) {
    context.system.eventStream.publish(DownloadComplete(summary))
  }

  def handleResourceChange(summary: Summary) {
    context.system.eventStream.publish(ResourceChanged(summary))
  }
}