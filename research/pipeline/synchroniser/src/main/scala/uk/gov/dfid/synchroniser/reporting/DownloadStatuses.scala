package uk.gov.dfid.reporting

import org.apache.http.HttpResponse

object DownloadStatuses {

  trait DownloadStatus
  case object New                                 extends DownloadStatus
  case object Updated                             extends DownloadStatus
  case object NotUpdated                          extends DownloadStatus
  case object Deleted                             extends DownloadStatus
  case class  BadResponse(response: HttpResponse) extends DownloadStatus
  case class  Error(ex: Throwable)                extends DownloadStatus
  case object Invalid                             extends DownloadStatus

}
