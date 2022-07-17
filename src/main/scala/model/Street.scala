package model

import io.circe.{Encoder, Json}
import io.circe.syntax.EncoderOps

final case class Street(name: String) extends AnyVal

object Street {
  implicit val encoder: Encoder[Street] = _.name.asJson

}
