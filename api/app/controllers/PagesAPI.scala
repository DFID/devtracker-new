package controllers

import play.api.mvc._

object PagesAPI extends Controller {

  def country(code:String) = Action { request =>
    Ok(PagesData.countryPageData(code).toString)
  }

  def countryProjects(code:String) = Action { request =>
    Ok(PagesData.countryProjectsPageData(code).toString)
  }

  def allDocumentsForAllProjects() = Action { request =>
    Ok(PagesData.allDocumentsForAllProjects().toString)
  }
}
