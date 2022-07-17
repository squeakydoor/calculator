package model

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

final case class Intersection(avenue: Avenue, street: Street)

object Intersection {
  implicit val encoder: Encoder[Intersection] = deriveEncoder
}
