package controllers

import play.api.mvc._
import play.api.libs.json.{JsArray, Json}
import play.api.libs.json.Json._
import scala.io._


object JSONDataReader extends Controller {
  def countries() = {
    Json.parse(Source.fromFile("conf/ContentJson/countries.json").mkString)
  }

  def sectorHierarchies() = {
    Json.parse(Source.fromFile("conf/ContentJson/sector-hierarchies.json").mkString)
  }

  def sectorHierarchiesWithCode(code: String) = {
    sectorHierarchies().asInstanceOf[JsArray].value.filter(s => (s \ "sectorCode").asOpt[Int] == Some(code.toInt))
  }

  def sectorHierarchiesHigherNameWithCode(code: String) = {

    val sector = sectorHierarchies().asInstanceOf[JsArray].value.filter(s => (s \ "sectorCode").asOpt[Int] == Some(code.toInt))

    if(sector.isEmpty)
      "Other"
    else
      sector(0) \ "highLevelName"
  }
  
  def countryWithCode(code: String) = {
    countries().asInstanceOf[JsArray].value.filter(c => (c \ "code").asOpt[String] == Some(code))(0)
  }

  def countriesRegionsStatsWithCode(code: String) = {
    val stats = Json.parse(Source.fromFile("conf/ContentJson/stats.json").mkString)
    val statsWithCountryCode = stats.asInstanceOf[JsArray].value.filter(c => (c \ "code").asOpt[String] == Some(code))
    Json.obj("stats" -> statsWithCountryCode)
  }

  def resultsCount(code: String) = {
    val js = Json.parse(Source.fromFile("conf/ContentJson/results.json").mkString)
    val count = js.asInstanceOf[JsArray].value.count(c => (c \ "code").asOpt[String] == Some(code))
    Json.obj("resultsCount" -> count)
  }
}


