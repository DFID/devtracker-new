package controllers

import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._

object JSONDataApi extends Controller {

  def countries() = Action { request =>
    Async{
      Future{
        Ok(JSONDataReader.countries())
      }
    }
  }

  def sectorHierarchies() = Action { request =>
    Async{
      Future{
        Ok(JSONDataReader.sectorHierarchies().toString()).as("text/json")
      }
    }
  }
  def sectorHierarchiesWithCode(code: String) = Action { request =>
    Async{
      Future{
        Ok(JSONDataReader.sectorHierarchiesWithCode(code).toString()).as("text/json")
      }
    }
  }
  def sectorHierarchiesHigherNameWithCode(code: String) = Action { request =>
    Async{
      Future{
        Ok(JSONDataReader.sectorHierarchiesHigherNameWithCode(code).toString()).as("text/json")
      }
    }
  }
  def countryWithCode(code: String) = Action { request =>
    Async{
      Future{
        Ok(JSONDataReader.countryWithCode(code))
      }
    }
  }
  def resultsCount(code:String) = Action { request =>
    Async{
      Future{
        Ok(JSONDataReader.resultsCount(code).toString()).as("text/json")
      }
    }
  }

  def countriesRegionsStatsWithCode(code:String) = Action { request =>
    Async{
      Future{
        Ok(JSONDataReader.countriesRegionsStatsWithCode(code).toString()).as("text/json")
      }
    }
  }

  def sectorHierarchies(code:String) = Action { request =>
    Async{
      Future{
        Ok(JSONDataReader.resultsCount(code).toString()).as("text/json")
      }
    }
  }
}