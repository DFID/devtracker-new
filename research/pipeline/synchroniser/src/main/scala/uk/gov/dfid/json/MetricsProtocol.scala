package uk.gov.dfid.json

import com.codahale.metrics.{Timer, MetricRegistry, Counter}
import java.util
import spray.json._
import collection.JavaConversions._

object MetricsProtocol extends DefaultJsonProtocol {

  implicit object CounterJsonWriter extends JsonWriter[Counter] {
    def write(obj: Counter) = obj.getCount.toJson
  }

  implicit object TimerJsonWriter extends JsonWriter[Timer] {
    def write(obj: Timer) = JsObject(
      "count"             -> obj.getCount.toJson,
      "fifteenMinuteRate" -> obj.getFifteenMinuteRate.toJson,
      "fiveMinuteRate"    -> obj.getFiveMinuteRate.toJson,
      "meanRate"          -> obj.getMeanRate.toJson,
      "oneMinuteRate"     -> obj.getOneMinuteRate.toJson
    )
  }

  implicit def sortedMapWriter[T: JsonWriter] = new JsonWriter[util.SortedMap[String, T]] {
    def write(obj: util.SortedMap[String, T]) =
      obj.map(e => e._1 -> e._2.toJson).toMap.toJson
  }

  implicit object MetricRegistryJsonWriter extends RootJsonWriter[MetricRegistry] {
    def write(registry: MetricRegistry) = JsObject(
      "counters" -> registry.getCounters.toJson,
      "timers"   -> registry.getTimers.toJson
    )
  }
}