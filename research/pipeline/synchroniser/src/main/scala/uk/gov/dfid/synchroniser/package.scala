package uk.gov.dfid

import spray.json.DefaultJsonProtocol._
import scala.util.{Success, Failure, Try}
import uk.gov.dfid.reporting.DownloadStatuses._
import uk.gov.dfid.reporting.Summary
import uk.gov.dfid.synchroniser.support.Instrumented
import uk.gov.dfid.support.{FileSystemSupport, HttpSupport, IATIValidationSupport}
import spray.json.{JsValue, JsObject, JsArray}

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
      val dir     = mkdirp(location)
      val summary = downloadAllPackages(dir)

      onComplete(summary)
      summary
    }

    def downloadAllPackages(dir: String) = {
      datasets match {
        case Success(packageList) => {
          val downloads = packageList.flatMap(downloadPackage(_, dir))
          val deleted   = compareAndDeletePackages(dir, downloads)

          downloads ++ deleted
        }
        case Failure(ex) => {
          val error = Summary(Error(ex), urlForPackageList, "", "Getting packages", "")
          onResourceChange(error)
          List(error)
        }
      }
    }

    def downloadPackage(id: String, dir:String) = {
      dataset(id) match {
        case Success(results) => {
          val fileType = fileTypeFrom(results)
          resources(results).flatMap { resource =>
            urlFrom(resource).map { url =>
              val path = s"$dir/$id.xml"
              val result = download(url, path)
              val summaryWithResult: DownloadStatus => Summary = Summary(_, url, path, id, fileType)
              val summary = result match {
                case Error(_) => summaryWithResult(result)
                case _ if validate(path, fileType) => summaryWithResult(result)
                case _ => summaryWithResult(Invalid)
              }

              onResourceChange(summary)
              summary
            }
          }
        }
        case Failure(ex) =>
          val summary = Summary(Error(ex), urlForPackage(id),"", id, "")
          onResourceChange(summary)
          List(summary)
      }
    }
    private def datasets =
      json(urlForPackageList)
        .map(_.asJsObject.fields("result").convertTo[List[String]])

    private def dataset(id: String) =
      json(urlForPackage(id))
        .map(_.asJsObject.fields("result").asJsObject)
    
    private def urlForPackage(id: String) = s"http://www.iatiregistry.org/api/3/action/package_show?id=$id"

    private def urlForPackageList = "http://www.iatiregistry.org/api/3/action/package_list"

    private def fileTypeFrom(dataset: JsObject) =
      dataset.fields("extras")
        .convertTo[JsArray]
        .elements
        .map(_.asJsObject)
        .find(_.fields.get("key").exists(_.convertTo[String] == "filetype"))
        .map(_.fields("value").convertTo[String])
        .getOrElse("activity")

    private def resources(dataset: JsObject) =
      dataset.fields("resources")
        .convertTo[JsArray]
        .elements

    private def urlFrom(resource: JsValue) =
      resource.asJsObject
        .getFields("url")
        .map(_.convertTo[String])
        .map(escapeUrl)

    private def compareAndDeletePackages(dir: String, downloads: List[Summary]) = {
      ls(dir)
        .filter(_.endsWith(".xml"))
        .map(dir + "/" + _)
        .diff(downloads.map(_.path)).map { deleted =>
          val status = Try(rm(deleted))
            .map(_ => Deleted)
            .recover({ case e => Error(e) })
            .get
          val pkg = deleted.split("/").last.replaceAll("\\.xml$", "")
          val summary = Summary(status, deleted, deleted, pkg, "")
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
      } getOrElse {
        downloadToFile(url, path)
      }
    }

    private def downloadToFile(url: String, path: String) = {
      get(url) match {
        case Success(response) =>
          response.getStatusLine.getStatusCode match {
            case 200 =>
              save(response.getEntity.getContent, path)
              New
            case _ => BadResponse(response)
          }
        case Failure(ex) => Error(ex)
      }
    }
  }

}
