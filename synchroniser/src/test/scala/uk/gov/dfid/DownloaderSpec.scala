package uk.gov.dfid

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalamock.scalatest.MockFactory
import uk.gov.dfid.synchroniser.Downloader

class DownloaderSpec extends FunSpec with ShouldMatchers with MockFactory {

  describe("Downloader configuration") {

    it("should accept location as a config parameter") {
      val downloader = Downloader().to("./anotherplace")
      downloader.location should be("./anotherplace")
    }

    it("should accept a handler for completion") {
      var flag = false
      val downloader = Downloader().withCompletedHandler(_ => flag = true)
      downloader.onComplete(List.empty)
      flag should be(true)
    }

    it("should accept a handler for resource actions") {
      var flag = false
      val downloader = Downloader().withResourceChangedHandler(_ => flag = true)
      downloader.onResourceChange(null)
      flag should be(true)
    }
  }

}