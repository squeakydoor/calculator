package model

import io.circe.Encoder
import io.circe.syntax.EncoderOps

final case class Avenue(name: String) extends AnyVal

object Avenue {
  implicit val encoder: Encoder[Avenue] = _.name.asJson

}
