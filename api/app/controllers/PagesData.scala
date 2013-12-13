package controllers

  import play.api.mvc._
  import scala.concurrent.Future
  import play.api.libs.concurrent.Execution.Implicits._
  import play.api.libs.json._
import scala.io.Source

object PagesData extends Controller {

  val apiRootURL = "http://localhost:9000/api/"

  def getListOfCountries = {
    JSONDataReader.countries
  }

  def countryPageData(code: String) = {
        Json.parse(getJsonFromUrl("country/" + code)).asInstanceOf[JsObject]++
        Json.parse(getJsonFromUrl("country/budget/" + code)).asInstanceOf[JsObject]++
        Json.parse(getJsonFromUrl("country/resultsCount/" + code)).asInstanceOf[JsObject]++
        Json.parse(getJsonFromUrl("country/stats/" + code)).asInstanceOf[JsObject]++
        Json.obj("SectorBreakdown" -> Json.parse(getJsonFromUrl("country/sectorBreakdown/" + code)))++
        Json.obj("locations" -> Json.parse(getJsonFromUrl("country/locations/" + code)))++
        Json.obj("ProjectBudgetsByYear" -> Json.parse(getJsonFromUrl("country/projectBudgetsByYear/" + code)))++
        Json.parse(getJsonFromUrl("dfidTotalBudget")).asInstanceOf[JsObject]
  }

  def countryProjectsPageData(code: String) = {

    val allH1ProjectCodes = Json.parse(getJsonFromUrl("country/projects/projectsHierarchy1ForCountryCode/" + code)).asInstanceOf[JsArray]

    val allH1Projects = Json.parse(getJsonFromUrl("country/projects/allHierarchy1Project/")).asInstanceOf[JsArray]

    //filterAllH1Projects(allH1Projects, allH1ProjectCodes)

  }

  def allDocumentsForAllProjects() = {
    Json.parse(getJsonFromUrl("projects/documents"))
  }

  def getJsonFromUrl(apiExtention: String) = {
    val url = new java.net.URL(apiRootURL + apiExtention)
    Source.fromInputStream(url.openStream).getLines.mkString("\n")
  }

  // def filterAllH1Projects(allH1Projects: JsArray, allH1Ids: JsArray) =  {
  //   Logger.info("before = " )
  //   val result = allH1Ids.value.map(h => {
        
  //       val projectForId = allH1Projects.value.filter(t => (t \ "iati-id") == h)(0).asInstanceOf[JsObject]
  //       projectForId
        
  //     }
  //   )

  //   result
  //   //.reduce(_++_).toString()
  // }


  // def combineJson() = {
  //   val processedBaseData = processBasicJsonString().value
  //   val processedBudget = processBudgetsJsonString().value
  //   val processedComponents = processComponentsJsonString().value
  //   processedBaseData.map( j => {
  //       val base = j.asInstanceOf[JsObject]

  //       val id = (base \ "id")
  //       val basesbudget = processedBudget.filter( b => (b \ "id") == id )(0).asInstanceOf[JsObject]
  //       val basesComponent = processedComponents.filter(c => (c \ "id") == id)(0).asInstanceOf[JsObject]

  //       base++basesbudget++basesComponent
  //     }
  //   )
  // }
}