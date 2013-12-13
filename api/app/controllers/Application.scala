package controllers

import play.api._
import play.api.mvc._
import basex._

object Application extends Controller with BaseXSupport {

  def activity = Action { request =>

    val reporting = request.getQueryString("reporting-org").getOrElse("")
    val (start, limit) = getPage(request)

    Async {
      withSession { session =>
        session.bind(
          "$start"         -> start,
          "$limit"         -> limit,
          "$reporting-org" -> reporting
        )

        Ok(session.run("activities")).as("text/xml")
      }
    }
  }

  private def getPage(request: Request[_]) = {
    val start = request.getQueryString("start").map(_.toInt).getOrElse(1)
    val limit = request.getQueryString("limit").map(_.toInt).getOrElse(50)

    start -> limit
  }
}