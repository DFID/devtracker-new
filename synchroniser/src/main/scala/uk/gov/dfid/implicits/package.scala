package uk.gov.dfid

import org.neo4j.graphdb.NotFoundException
import scala.util.Try
import com.typesafe.config.Config
import org.mashupbots.socko.events.{HttpResponseStatus, HttpResponseMessage}


package object implicits {

  implicit class ConsoleColorise(val str: String) extends AnyVal {
    import Console._

    def black     = s"$BLACK$str"
    def red       = s"$RED$str"
    def green     = s"$GREEN$str"
    def yellow    = s"$YELLOW$str"
    def blue      = s"$BLUE$str"
    def magenta   = s"$MAGENTA$str"
    def cyan      = s"$CYAN$str"
    def white     = s"$WHITE$str"

    def blackBg   = s"$BLACK_B$str"
    def redBg     = s"$RED_B$str"
    def greenBg   = s"$GREEN_B$str"
    def yellowBg  = s"$YELLOW_B$str"
    def blueBg    = s"$BLUE_B$str"
    def magentaBg = s"$MAGENTA_B$str"
    def cyanBg    = s"$CYAN_B$str"
    def whiteBg   = s"$WHITE_B$str"
  }

  /**
   * Sugar for working with string
   * @param stringValue
   */
  implicit class SuperString(stringValue : String) {

    /**
     * Super cludge that attempts to parse a string of essentially anything
     * into something else.  Tries Int, Double and Boolean
     * @return Something, anything, magic
     */
    def mungeToType =
      Try(stringValue.toLong) orElse
        Try(stringValue.toDouble) orElse
        Try(stringValue.toBoolean) getOrElse
        stringValue
  }

  /**
   * Sugar for working with XML Nodes
   * @param node
   */
  implicit class SuperXmlNode(node: xml.Node) {

    /**
     * Simple check on a nod to determine if it is a simple text node
     * @return
     */
    def isTextNode = node.label.equals("#PCDATA")
  }

  implicit class SuperConfig(config: Config) {

    def tryGetString(path: String) = {
      if(config.hasPath(path)){
        Some(config.getString(path))
      } else {
        None
      }
    }
  }

  implicit class SuperHttpResponseMessage(response: HttpResponseMessage) {

    import spray.json._

    def json[T](obj: T)(implicit writer: JsonWriter[T]) {
      response.write(obj.toJson.toString, "application/json")
    }
  }
}
