package controllers

import play.api.mvc._
import basex._
import play.api.libs.json.{JsArray, Json}
import java.util.Calendar
import java.text.SimpleDateFormat
import scala.io.Source

object Aggregator extends Controller with BaseXSupport {

  val apiRootURL = "http://localhost:9000/api/"

  def countryBudget(code: String) = Action.async { request =>
    val (start, end) = currentFinancialYear

    withSession { session =>
      session.bind(
        "$country_code" -> code,
        "$start_date" -> start,
        "$end_date" -> end
      )
      Ok(session.run("country_summary/country_budget")).as("text/json")
    }
  }

  def locations(code: String) = Action.async { request =>
    withSession { session =>
      session.bind(
        "$country_code" -> code
      )
      Ok(session.run("country_summary/locations")).as("text/json")
    }
  }

  def projectBudgetByYear(code: String) = Action.async { request =>
    withSession { session =>
      session.bind(
        "$country_code" -> code
      )
      Ok(session.run("country_summary/project_budget_by_year")).as("text/json")
    }
  }

  def projectsStats(code: String) = Action.async { request =>
    withSession { session =>
      session.bind(
        "$country_code" -> code
      )
     Ok(session.run("country_summary/projects_stats")).as("text/json")
    }
  }

  def sectorBreakdown(code: String) = Action.async { request =>
    withSession { session =>
      session.bind("$country_code" -> code)
      val sectorBreakdown = session.run("country_summary/sector_breakdown")

      Ok(
        // [todo] - [JH] - I have no clue what this does or why its written like this but it seems wrong to serialise, parse, transform, deserialise to string, regex replace things reparse then convert to string again.
        Json.toJson(
          Json.parse(sectorBreakdown).as[JsArray].value.map(
            x =>  Json.obj("percentage" -> x.\("percentage"))++
                  Json.obj("sector" ->
                            getJsonFromUrl("sectorHierarchyName/" + (x.\("code")
                          ).toString().replaceAll("\"", "")).replaceAll("\"", "")
                  )
          )
        ).toString()
      ).as("text/json")
    }
  }

  def dfidCountries = Action.async { request =>
    withSession { session =>
      Ok(session.run("dfid_countries")).as("text/json")
    }
  }

  def dfidTotalBudget = Action.async { request =>
    withSession { session =>
      Ok(session.run("dfid_total_budget")).as("text/json")
    }
  }

  def project() = Action.async { request =>
    withSession { session =>
      // [todo] - [??] - to be changed
      // [todo] - [JH] - needs to be changed to what?
      Ok(session.run("dfid_total_budget")).as("text/json")
    }
  }

  def projectsHierarchy1ForCountryCode(code: String) = Action.async { request =>
    withSession { session =>
      Ok(session.run("countryProjects/AllH1ProjectIdsForCountryCode")).as("text/json")
    }
  }

  def allHierarchy1Project() = Action.async { request =>
    withSession { session =>
      Ok(session.run("countryProjects/AllHierarchy1Projects")).as("text/json")
    }
  }

  def allDocumentsForAllProjects() = Action.async { request =>
    withSession { session =>
      Ok(session.run("projects/documents/allDocumentsForAllProjects")).as("text/json")
    }
  }

  def currentFinancialYear = {
    val now = Calendar.getInstance().getTime()

    val monthFormat = new SimpleDateFormat("MM")
    val yearFormat =  new SimpleDateFormat("YYYY")

    val currentMonth = monthFormat.format(now).toInt
    val currentYear = yearFormat.format(now).toInt

    if (currentMonth < 4) {
      s"${currentYear-1}-04-01" -> s"${currentYear}-03-31"
    } else {
      s"${currentYear}-04-01" -> s"${currentYear + 1}-03-31"
    }
  }

  def getJsonFromUrl(apiExtention: String) = {
    val url = new java.net.URL(apiRootURL + apiExtention)
    Source.fromInputStream(url.openStream).getLines.mkString("\n")
  }

  def unique[A](ls: List[A]) = {
    def loop(set: Set[A], ls: List[A]): List[A] = ls match {
      case hd :: tail if set contains hd => loop(set, tail)
      case hd :: tail => hd :: loop(set + hd, tail)
      case Nil => Nil
    }

    loop(Set(), ls)
  }
}
