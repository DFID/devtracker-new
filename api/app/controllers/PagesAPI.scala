package controllers

import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._

/**
 * Created with IntelliJ IDEA.
 * User: endam
 * Date: 12/08/2013
 * Time: 06:54
 * To change this template use File | Settings | File Templates.
 */
object PagesAPI extends Controller {

  def country(code:String) = Action { request =>
    Async{
      Future{
        Ok(PagesData.countryPageData(code).toString)
      }
    }
  }

  def countryProjects(code:String) = Action { request =>
	Async{
      Future{
        Ok(PagesData.countryProjectsPageData(code).toString)
      }
    }
  }

  def allDocumentsForAllProjects() = Action { request =>
    Async{
      Future{
        Ok(PagesData.allDocumentsForAllProjects.toString)
      }
    }
  }
}
