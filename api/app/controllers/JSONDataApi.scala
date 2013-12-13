package controllers

import play.api.mvc._

object JSONDataApi extends Controller {

  def countries() = Action { request =>
    Ok(JSONDataReader.countries())
  }

  def sectorHierarchies() = Action { request =>
    Ok(JSONDataReader.sectorHierarchies().toString()).as("text/json")
  }

  def sectorHierarchiesWithCode(code: String) = Action { request =>
    Ok(JSONDataReader.sectorHierarchiesWithCode(code).toString()).as("text/json")
  }

  def sectorHierarchiesHigherNameWithCode(code: String) = Action { request =>
    Ok(JSONDataReader.sectorHierarchiesHigherNameWithCode(code).toString).as("text/json")
  }

  def countryWithCode(code: String) = Action { request =>
    Ok(JSONDataReader.countryWithCode(code))
  }

  def resultsCount(code:String) = Action { request =>
    Ok(JSONDataReader.resultsCount(code).toString()).as("text/json")
  }

  def countriesRegionsStatsWithCode(code:String) = Action { request =>
    Ok(JSONDataReader.countriesRegionsStatsWithCode(code).toString()).as("text/json")
  }

  def sectorHierarchies(code:String) = Action { request =>
    Ok(JSONDataReader.resultsCount(code).toString()).as("text/json")
  }
}