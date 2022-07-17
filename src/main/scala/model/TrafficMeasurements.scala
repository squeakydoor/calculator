package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

final case class TrafficMeasurements(
    measurementTime: Int,
    measurements: Seq[Measurements]
)

object TrafficMeasurements {
  implicit val decoder: Decoder[TrafficMeasurements] = deriveDecoder
  implicit val encoder: Encoder[TrafficMeasurements] = deriveEncoder
}
