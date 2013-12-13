package uk.gov.dfid.support

import scala.xml.XML
import javax.xml.validation.SchemaFactory
import javax.xml.transform.stream.StreamSource
import javax.xml.XMLConstants
import java.io.{File, FileInputStream}
import org.w3c.dom.ls.LSResourceResolver
import uk.gov.dfid.Input

trait  IATIValidationSupport {

  def validate(path: String, filetype: String) = {

    try {
      val xml = XML.loadFile(path)

      val header = filetype match {
        case "activity"     => "activities"
        case "organisation" => "organisations"
      }

      val version = (xml \ s"iati-$header" \ "@version")
        .headOption
        .map(_.text)
        .getOrElse("1.02")

      val xsdFile = new File(s"./src/main/resources/xsd/$version/iati-$header-schema.xsd")

      if(xsdFile.exists) {
        val xsd = new FileInputStream(xsdFile)

        try {
          val factory   = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
          factory.setResourceResolver(new ResourceResolver(version))
          val schema    = factory.newSchema(new StreamSource(xsd))
          val validator = schema.newValidator
          validator.validate(new StreamSource(new FileInputStream(path)))
          true
        } catch {
          case e: Throwable =>
            false
        }
      } else {
        false
      }
    } catch {
      case e: Throwable =>
        false
    }
  }

  class ResourceResolver(version: String) extends LSResourceResolver {
    def resolveResource(`type`: String, namespaceURI: String, publicId: String, systemId: String, baseURI: String) = {
      val stream = new FileInputStream(new File(s"./src/main/resources/xsd/$version/$systemId"))
      new Input(publicId, systemId, stream)
    }
  }
}


