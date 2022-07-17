package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

final case class Measurements(
    startAvenue: String,
    startStreet: String,
    transitTime: Double,
    endAvenue: String,
    endStreet: String
)

object Measurements {
  implicit val decoder: Decoder[Measurements] = deriveDecoder
  implicit val encoder: Encoder[Measurements] = deriveEncoder
}
