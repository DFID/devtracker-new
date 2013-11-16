package uk.gov.dfid.reporting

import uk.gov.dfid._
import implicits._
import uk.gov.dfid.reporting.DownloadStatuses._

object ConsoleReporter {

  def report(summary: Summary) {
    val pkg = summary.`package`

    summary.result match {
      case New        => println("  ✔ ".green + pkg.white)
      case NotUpdated => println("  - ".yellow + pkg.white)
      case Updated    => println("  ↑ ".green + pkg.white)
      case Deleted    => println("  ← ".red + summary.path.white)
      case Invalid    => {
        println("  ✘ ".red + pkg.white)
        println("    Failed IATI Validation".red + "".white)
      }
      case Error(ex)  => {
        println("  ✘ ".red + pkg.white)
        println("    " + ex.toString.red + "".white)
      }
      case BadResponse(response) => {
        val status     = response.getStatusLine
        val statusCode = status.getStatusCode
        val statusText = status.getReasonPhrase

        println("  ✘ ".red + pkg.white)
        println(s"    $statusCode $statusText".red + "".white)
      }
    }

  }

  def summarise(summary: List[Summary]) {

    println()

    // handle the status that are objects
    summary.groupBy(_.result).collect {
      case (New        , items) => println(f"${items.size}%8d".green  + " New Resources".white)
      case (NotUpdated , items) => println(f"${items.size}%8d".green  + " Resources Not Updated".white)
      case (Updated    , items) => println(f"${items.size}%8d".green  + " Resources Updated".white)
      case (Deleted    , items) => println(f"${items.size}%8d".yellow + " Resources Deleted".white)
      case (Invalid    , items) => println(f"${items.size}%8d".red    + " Resources Invalid".white)
    }

    val badResponses = summary.filter(_.result.isInstanceOf[BadResponse]).size
    if(badResponses > 0){
      println(f"$badResponses%8d".red + " Bad Responses".white)
    }

    val errors = summary.filter(_.result.isInstanceOf[Error]).size
    if(errors > 0) {
      println(f"$errors%8d".red + " Errors".white)
    }

    println()
  }

}
