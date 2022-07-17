package repo

import cats.Functor
import cats.effect.kernel.Sync
import cats.effect.Ref
import cats.implicits._
import model.{Avenue, Intersection, Junction, Measurements, Street}
import scalax.collection.Graph
import scalax.collection.GraphPredef._

import scala.annotation.tailrec

/** Using an underlying weighted directed graph implementation,
  *  represents intersections of streets and avenues as nodes
  * and the connections between them as edges.
  */
case class TrafficGrid[F[_]: Functor](
    graphRef: Ref[F, Graph[Intersection, Junction]]
) {

  /** Adds a measurement between two intersections. If adding a measurement for an existing
    * segment, the stored value will be the average.
    * @param from Starting point
    * @param to Ending point
    * @param time The time spent between them
    * @return updated grid
    */
  def add(
      from: Intersection,
      to: Intersection,
      time: Double
  ): F[Graph[Intersection, Junction]] =
    graphRef.getAndUpdate { graph => TrafficGrid.add(graph, from, to, time) }

  def add(measurements: Seq[Measurements]): F[Graph[Intersection, Junction]] =
    graphRef.getAndUpdate { graph =>
      val data = measurements
        .map(measurement =>
          (
            Intersection(
              Avenue(measurement.startAvenue),
              Street(measurement.startStreet)
            ),
            Intersection(
              Avenue(measurement.endAvenue),
              Street(measurement.endStreet)
            ),
            measurement.transitTime
          )
        )
        .toList

      @tailrec
      def buildGraph(
          ms: List[(Intersection, Intersection, Double)],
          graph: Graph[Intersection, Junction]
      ): Graph[Intersection, Junction] = {
        ms match {
          case (from, to, time) :: tail =>
            buildGraph(tail, TrafficGrid.add(graph, from, to, time))
          case Nil => graph
        }
      }
      buildGraph(data, graph)
    }

  /** @param from Starting point
    * @param to Ending point
    * @return If both intersections are part of the grid, tries to find a lowest-cost path
    *         between them (None if impossible, Some(Path) if possible).
    */
  def findShortestPath(
      from: Intersection,
      to: Intersection
  ): F[Either[TrafficGrid.Error, Option[Graph[Intersection, Junction]#Path]]] =
    graphRef.get.map { grid =>
      TrafficGrid.findShortestPath(grid, from, to).map(_.map(_.value))
    }

}

object TrafficGrid {
  sealed trait Error
  object Error {
    case object InvalidIntersection extends Error
  }

  def make[F[_]: Sync]: F[TrafficGrid[F]] =
    Ref[F].of(Graph[Intersection, Junction]()).map(TrafficGrid(_))

  def findShortestPath(
      grid: Graph[Intersection, Junction],
      from: Intersection,
      to: Intersection
  ): Either[Error, Option[Graph[Intersection, Junction]#Path]] =
    (grid.find(from), grid.find(to)) match {
      case (Some(a), Some(b)) => a.shortestPathTo(b).asRight
      case _                  => Error.InvalidIntersection.asLeft
    }

  def add(
      graph: Graph[Intersection, Junction],
      from: Intersection,
      to: Intersection,
      time: Double
  ): Graph[Intersection, Junction] = {
    val edges = graph.edges
    val maybeOldEdge = edges.find(edge =>
      edge.value.edge.fromIntersection == from && edge.value.edge.toIntersection == to
    )
    // used to compute the average
    val dataPoints = maybeOldEdge.map(_.dataPoints + 1).getOrElse(1)
    // weight is the average
    val newWeight = maybeOldEdge
      .map(edge => (edge.weight * edge.dataPoints + time) / dataPoints)
      .getOrElse(time)
    val newEdge = Junction(from, to, dataPoints, newWeight)
    maybeOldEdge.fold(graph + newEdge)(oldEdge => graph - oldEdge + newEdge)
  }
}
