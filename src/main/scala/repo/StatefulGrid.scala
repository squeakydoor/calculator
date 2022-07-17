//package repo
//
//import cats.Applicative
//import cats.data.{State, StateT}
//import cats.implicits._
//import model.{Intersection, Junction}
//import scalax.collection.Graph
//import scalax.collection.GraphPredef._
//
//case class StatefulGrid[F[_]: Applicative](
//    graph: Graph[Intersection, Junction]
//) {
//  sealed trait Error
//
//  object Error {
//    case object InvalidIntersection extends Error
//  }
//
//  def add(
//      from: Intersection,
//      to: Intersection,
//      time: Double
//  ) = StateT[F, Graph[Intersection, Junction], Unit] { graph =>
//    {
//      val edges = graph.edges
//      val maybeOldEdge = edges.find(edge =>
//        edge.value.edge.fromIntersection == from && edge.value.edge.toIntersection == to
//      )
//      val dataPoints = maybeOldEdge.map(_.dataPoints + 1).getOrElse(1)
//      val newWeight =
//        maybeOldEdge
//          .map(edge => (edge.weight * edge.dataPoints + time) / dataPoints)
//          .getOrElse(time)
//      val newEdge = Junction(from, to, dataPoints, newWeight)
//      (
//        maybeOldEdge.fold(graph + newEdge)(oldEdge =>
//          graph - oldEdge + newEdge
//        ),
//        ()
//      ).pure[F]
//    }
//  }
//
//  def findShortestPath(from: Intersection, to: Intersection) =
//    for {
//      graph <- StateT.get[F, Graph[Intersection, Junction]]
//      result =
//        (graph.find(from), graph.find(to)) match {
//          case (Some(a), Some(b)) =>
//            State.pure(a.shortestPathTo(b).asRight[Error])
//          case _ => State.pure(Left(Error.InvalidIntersection))
//        }
//    } yield result
//
//}
//
//object StatefulGrid {
//  def make[F[_]: Applicative]: StatefulGrid[F] = StatefulGrid[F](
//    Graph[Intersection, Junction]()
//  )
//}
