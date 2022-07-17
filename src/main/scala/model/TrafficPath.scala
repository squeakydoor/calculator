package model

import cats.implicits.catsSyntaxOptionId
import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}
import scalax.collection.Graph

final case class TrafficPath(
    startingIntersection: Option[Intersection],
    endingIntersection: Option[Intersection],
    roadSegments: Option[Seq[RoadSegment]]
) {
  def transitTime: Option[Double] = roadSegments.map(_.map(_.transitTime).sum)
}

object TrafficPath {

  implicit val encoder: Encoder[TrafficPath] = path =>
    Json.obj(
      ("startingIntersection", path.startingIntersection.asJson),
      ("endingIntersection", path.endingIntersection.asJson),
      ("roadSegments", path.roadSegments.asJson),
      ("totalTransitTime", path.transitTime.asJson)
    )

  def empty: TrafficPath = TrafficPath(None, None, None)

  def from(path: Graph[Intersection, Junction]#Path): TrafficPath =
    TrafficPath(
      path.edges.head.fromIntersection.value.some,
      path.edges.last.toIntersection.value.some,
      path.edges.map(RoadSegment.from).toSeq.some
    )
}
