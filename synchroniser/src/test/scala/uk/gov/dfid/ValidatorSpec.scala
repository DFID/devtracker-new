package uk.gov.dfid

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import uk.gov.dfid.support.IATIValidationSupport

class ValidatorSpec extends FunSpec with ShouldMatchers {


  class ValidatorStub

  describe("Validating Activity Files") {
    it("should validate a valid activity file correctly for v1.01") {
      val v = new ValidatorStub with IATIValidationSupport
      v.validate("./src/test/resources/valid-activity-1.01.xml", "activity") should be (true)
    }

    it("should validate a valid activity file correctly for v1.02") {
      val v = new ValidatorStub with IATIValidationSupport
      v.validate("./src/test/resources/valid-activity-1.02.xml", "activity") should be (true)
    }

    it("should validate a valid activity file correctly when there are no versions") {
      val v = new ValidatorStub with IATIValidationSupport
      v.validate("./src/test/resources/valid-activity-no-version.xml", "activity") should be (true)
    }

    it("should not validate a malformed file") {
      val v = new ValidatorStub with IATIValidationSupport
      v.validate("./src/test/resources/malformed.xml", "activity") should be (false)
    }

    it("should not validate a non existent file") {
      val v = new ValidatorStub with IATIValidationSupport
      v.validate("./src/test/resources/nope.xml", "activity") should be (false)
    }
  }

  describe("Validating Organisation Files") {
    it("should validate a valid Organisation file correctly for v1.01") {
      val v = new ValidatorStub with IATIValidationSupport
      v.validate("./src/test/resources/valid-org-1.01.xml", "organisation") should be (true)
    }

    it("should validate a valid Organisation file correctly for v1.02") {
      val v = new ValidatorStub with IATIValidationSupport
      v.validate("./src/test/resources/valid-org-1.02.xml", "organisation") should be (true)
    }

    it("should validate a valid Organisation file correctly when there are no versions") {
      val v = new ValidatorStub with IATIValidationSupport
      v.validate("./src/test/resources/valid-org-no-version.xml", "organisation") should be (true)
    }
  }
}
