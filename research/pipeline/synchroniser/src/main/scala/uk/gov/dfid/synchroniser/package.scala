package uk.gov.dfid

import spray.json.DefaultJsonProtocol._
import scala.util.{Success, Failure, Try}
import uk.gov.dfid.reporting.DownloadStatuses._
import uk.gov.dfid.reporting.Summary
import uk.gov.dfid.utils.{HttpSupport, FileSystemSupport}
import uk.gov.dfid.synchroniser.support.Instrumented
import uk.gov.dfid.support.IATIValidationSupport

package object synchroniser {

  /**
   * Shorthand type aliases for the event handlers
   */
  type ResourceChangeHandler = Summary => Unit
  type CompletedHandler = List[Summary] => Unit

  /**
   * Downloader class represents the main entry point for the downloader
   * @param location configurable location for downloaded files
   * @param onResourceChange Handler called when a  resource changes
   * @param onComplete Handler called when the download is complete
   */
  case class Downloader(
    private[dfid] val location         : String                = "data",
    private[dfid] val onResourceChange : ResourceChangeHandler = (_) => Unit,
    private[dfid] val onComplete       : CompletedHandler      = (_) => Unit
  ) extends HttpSupport with FileSystemSupport with Instrumented with IATIValidationSupport {

    /**
     * Builds and instance of the current downloader with a new resource
     * change handler
     * @param handler The new handler
     * @return A new instance of Downloader
     */
    def withResourceChangedHandler(handler: ResourceChangeHandler) =
      copy(onResourceChange = handler)

    /**
     * Builds and instance of the current downloader with a new handler
     * @param handler The new handler
     * @return A new instance of Downloader
     */
    def withCompletedHandler(handler: CompletedHandler) =
      copy(onComplete = handler)

    /**
     * Builds and instance of the current downloader with a new location
     * @param location The new download location
     * @return A new instance of Downloader
     */
    def to(location: String) =
      copy(location = location)

    def execute = metrics.timer("download-time").time {
      val url = "http://www.iatiregistry.org/api/1/rest/group"
      val summary = json(url) match {
          case Success(groups) => {
            groups.convertTo[List[String]].flatMap { group =>
              val url = s"http://www.iatiregistry.org/api/1/rest/group/$group"
              json(url) match {
                case Success(groupJson) => {
                  val dir = mkdirp(s"$location/$group")
                  val downloads = groupJson.asJsObject
                    .fields("packages")
                    .convertTo[List[String]]
                    .flatMap { pkg =>
                      val url = s"http://www.iatiregistry.org/api/1/rest/package/$pkg"
                      json(url) match {
                        case Success(pkgJson) => {
                          val obj = pkgJson.asJsObject
                          val filetype = obj.getFields("extras")
                            .headOption
                            .flatMap { extras =>
                              extras.asJsObject
                                .fields
                                .get("filetype")
                                .map(_.convertTo[String])
                            }
                            .getOrElse("activity")

                          obj.fields
                            .get("download_url")
                            .map(_.convertTo[String])
                            .map(escapeUrl)
                            .map { url =>
                              val path = s"$dir/$pkg.xml"
                              val result = download(url, path)
                              val valid = validate(path, filetype)
                              val summary = if(valid) {
                                Summary(result, url, path, group, pkg, filetype)
                              } else {
                                Summary(Invalid, url, path, group, pkg, filetype)
                              }

                              onResourceChange(summary)
                              summary
                            }
                        }
                        case Failure(ex) => Some(Summary(Error(ex),url,"", group, pkg, ""))
                      }
                    }

                  val deleted = compareAndDeletePackages(dir, downloads, group)

                  downloads ++ deleted
                }
                case Failure(ex) => {
                  val error = Summary(Error(ex),url, "", group, s"Getting packages for $group", "")
                  onResourceChange(error)
                  List(error)
                }
              }
            }
          }
          case Failure(ex) => {
            val error = Summary(Error(ex), url, "", "", "Getting groups from registry", "")
            onResourceChange(error)
            List(error)
          }
        }

      onComplete(summary)
      summary
    }

    private def compareAndDeletePackages(dir: String, downloads: List[Summary], group: String) = {
      ls(dir)
        .filter(_.endsWith(".xml"))
        .map(dir + "/" + _)
        .diff(downloads.map(_.path)).map { deleted =>
          val status = Try(rm(deleted))
            .map(_ => Deleted)
            .recover({ case e => Error(e) })
            .get
          val pkg = deleted.split("/").last.replaceAll("\\.xml$", "")
          val summary = Summary(status, deleted, deleted, group, pkg, "")
          onResourceChange(summary)
          summary
        }
    }

    private def download(url: String, path: String) = {
      Try(downloadIfNewer(url, path))
        .recover({case e => Error(e)})
        .get
    }

    private def downloadIfNewer(url: String, path: String) = {

      fileLastModified(path).map { lastModifiedFile =>
        head(url) match {
          case Success(response) => {
            response.getStatusLine.getStatusCode match {
              case 200 => {

                val outOfDate = urlLastModified(response)
                  .map(lastModifiedUrl => lastModifiedUrl > lastModifiedFile)
                  .getOrElse(true)

                if(outOfDate) {
                  downloadToFile(url, path) match {
                    case New   => Updated
                    case other => other
                  }
                } else {
                  NotUpdated
                }

              }
              case _ => BadResponse(response)
            }
          }
          case Failure(ex) => Error(ex)
        }
      }.getOrElse {
        downloadToFile(url, path)
      }
    }

    private def downloadToFile(url: String, path: String) = {
      get(url) match {
        case Success(response) => {
          response.getStatusLine.getStatusCode match {
            case 200 => {
              val stream = response.getEntity.getContent
              save(stream, path)
              New
            }
            case _ => BadResponse(response)
          }
        }
        case Failure(ex) => Error(ex)
      }

    }
  }

}
