package uk.gov.dfid.actors

import akka.actor.Actor
import uk.gov.dfid.reporting.DownloadStatuses._
import uk.gov.dfid.basex.BaseXClient
import java.io.FileInputStream
import uk.gov.dfid.synchroniser.support.Instrumented
import uk.gov.dfid.actors.Commands.DownloadComplete
import uk.gov.dfid.actors.Commands.ResourceChanged

class Indexer extends Actor with Instrumented {

  // TODO: Move credentials and port to external config.
  lazy val session = new BaseXClient("localhost", 1984, "admin", "admin")

  override def preStart() {
    session.execute("check iati")
    session.execute("set skipcorrupt true")
  }

  def receive = {
    case ResourceChanged(summary) => {
      val indexName = summary.`package`
      summary.result match {
        case New        => index(indexName, summary.path)
        case Updated    => {
          unindex(indexName)
          index(indexName, summary.path)
        }
        case NotUpdated => // Do Nothing
        case _          => unindex(indexName) // TODO aggressive unindex on failure - does this affect performance?
      }
    }
    case DownloadComplete => session.execute("optimize")
  }

  def index(filename: String, path: String) {
    session.add(filename, new FileInputStream(path))
  }

  def unindex(filename: String) {
    session.execute(s"delete $filename.xml")
  }
}
