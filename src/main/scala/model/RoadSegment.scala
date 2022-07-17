package model

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import scalax.collection.Graph

final case class RoadSegment(
    startingIntersection: Intersection,
    endingIntersection: Intersection,
    transitTime: Double
)

object RoadSegment {
  implicit val encoder: Encoder[RoadSegment] = deriveEncoder

  def from(edge: Graph[Intersection, Junction]#EdgeT): RoadSegment =
    RoadSegment(
      edge.fromIntersection.value,
      edge.toIntersection.value,
      edge.weight
    )
}
