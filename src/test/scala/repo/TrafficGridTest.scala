package repo

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import model.{Avenue, Intersection, Junction, Street}
import org.scalatest.freespec.AsyncFreeSpec
import scalax.collection.Graph

class TrafficGridTest extends AsyncFreeSpec with AsyncIOSpec {

  "TrafficGrid" - {

    val validIntersectionA1 = Intersection(Avenue("A"), Street("1"))
    val validIntersectionB1 = Intersection(Avenue("B"), Street("1"))
    val unreachableIntersection = Intersection(Avenue("C"), Street("1"))
    val invalidIntersection = Intersection(Avenue("DC"), Street("1"))
    val timeA1B1 = 14.doubleValue
    val grid = Graph[Intersection, Junction](
      unreachableIntersection,
      Junction(validIntersectionA1, validIntersectionB1, 1, timeA1B1)
    )

    // IO testing
    "should find a path when intersections have a connection" in {
      val from = Intersection(Avenue("A"), Street("1"))
      val to = Intersection(Avenue("B"), Street("1"))
      val time = 20.doubleValue
      for {
        trafficGrid <- TrafficGrid.make[IO]
        _ <- trafficGrid.add(from, to, time)
        path <- trafficGrid.findShortestPath(from, to)
      } yield {
        assert(path.isDefined)
        assertResult(Right(Some(from)))(path.map(_.map(_.startNode)))
        assertResult(Right(Some(to)))(path.map(_.map(_.endNode)))
        assertResult(Right(Some(time)))(path.map(_.map(_.weight)))
      }
    }

    // Pure functions testing
    "should return None if no possible path" in {
      val noPath = TrafficGrid.findShortestPath(
        grid = grid,
        from = validIntersectionA1,
        to = unreachableIntersection
      )
      assertResult(Right(None))(noPath)
    }

    "should return error if invalid intersection" in {
      val errorPath = TrafficGrid.findShortestPath(
        grid = grid,
        from = invalidIntersection,
        to = validIntersectionB1
      )
      assertResult(Left(TrafficGrid.Error.InvalidIntersection))(errorPath)
    }
  }
}
