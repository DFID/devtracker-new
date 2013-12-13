package uk.gov.dfid.synchroniser.support

import uk.gov.dfid.Application
import nl.grons.metrics.scala.InstrumentedBuilder

trait Instrumented extends InstrumentedBuilder {
  val metricRegistry = Application.metrics
}
