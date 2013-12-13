package controllers

import play.api.mvc._
import play.api.libs.json._
import scala.io.Source

object PagesData extends Controller {

  val apiRootURL = "http://localhost:9000/api/"

  def getListOfCountries = {
    JSONDataReader.countries
  }

  def countryPageData(code: String) = {
    // [fix] - [JH] - This code has a funky smell to it
    Json.parse(getJsonFromUrl("country/" + code)).asInstanceOf[JsObject]++
    Json.parse(getJsonFromUrl("country/budget/" + code)).asInstanceOf[JsObject]++
    Json.parse(getJsonFromUrl("country/resultsCount/" + code)).asInstanceOf[JsObject]++
    Json.parse(getJsonFromUrl("country/stats/" + code)).asInstanceOf[JsObject]++
    Json.obj("SectorBreakdown" -> Json.parse(getJsonFromUrl("country/sectorBreakdown/" + code)))++
    Json.obj("locations" -> Json.parse(getJsonFromUrl("country/locations/" + code)))++
    Json.obj("ProjectBudgetsByYear" -> Json.parse(getJsonFromUrl("country/projectBudgetsByYear/" + code)))++
    Json.parse(getJsonFromUrl("dfidTotalBudget")).asInstanceOf[JsObject]
  }

  // [fix] - [JH] - This code was left incomplete
  def countryProjectsPageData(code: String) = {
    val allH1ProjectCodes = Json.parse(getJsonFromUrl("country/projects/projectsHierarchy1ForCountryCode/" + code)).asInstanceOf[JsArray]
    val allH1Projects = Json.parse(getJsonFromUrl("country/projects/allHierarchy1Project/")).asInstanceOf[JsArray]
  }

  def allDocumentsForAllProjects() = {
    Json.parse(getJsonFromUrl("projects/documents"))
  }

  // [fix] - [JH] - This seems like an extremely insecure way to handle loading of file and such
  // [review] - [JH] - getJsonFromUrl returns a string and then the JSON is being parsed outside the call  which seems odd
  def getJsonFromUrl(apiExtention: String) = {
    val url = new java.net.URL(apiRootURL + apiExtention)
    Source.fromInputStream(url.openStream).mkString("\n")
  }
}