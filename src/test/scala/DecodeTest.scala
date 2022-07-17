import Main.decodeMeasurements
import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import fs2.Stream
import model.Measurements
import org.scalatest.freespec.AsyncFreeSpec

class DecodeTest extends AsyncFreeSpec with AsyncIOSpec {
  "should decode measurements" in {

    val expectedMeasurement = Measurements("A", "1", 42.doubleValue, "B", "1")

    val measurements =
      s"""{
        |  "trafficMeasurements":
        |  [
        |    {
        |      "measurementTime":86544,
        |      "measurements": [
        |        {
        |          "startAvenue": "${expectedMeasurement.startAvenue}",
        |          "startStreet": "${expectedMeasurement.startStreet}",
        |          "transitTime": "${expectedMeasurement.transitTime}",
        |          "endAvenue":   "${expectedMeasurement.endAvenue}",
        |          "endStreet":   "${expectedMeasurement.endStreet}"
        |        },
        |        {"startAvenue":"B","startStreet":"1","transitTime":10,"endAvenue":"C","endStreet":"1"}
        |      ]
        |    }
        |  ]
        |}""".stripMargin

    val stream = Stream[IO, String](measurements)

    decodeMeasurements(stream).compile.lastOrError.map(measurements =>
      assertResult(expectedMeasurement)(measurements.measurements.head)
    )
  }
}
