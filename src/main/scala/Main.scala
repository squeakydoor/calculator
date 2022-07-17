import cats.effect.IO
import cats.effect.kernel.Sync
import cats.effect.unsafe.implicits.global
import cats.implicits._
import fs2.data.json._
import fs2.data.json.circe._
import fs2.data.json.codec._
import fs2.data.json.selector._
import fs2.io.file.{Files, Path}
import fs2.{RaiseThrowable, Stream, text}
import io.circe.syntax.EncoderOps
import model._
import repo.TrafficGrid

object Main extends App {

  def trafficStream[F[_]: Files: RaiseThrowable](
      fileName: String
  ): Stream[F, TrafficMeasurements] =
    Files[F]
      .readAll(Path(fileName))
      .through(text.utf8.decode)
      .through(decodeMeasurements[F])

  def decodeMeasurements[F[_]: RaiseThrowable](
      stringStream: Stream[F, String]
  ): Stream[F, TrafficMeasurements] =
    stringStream
      .through(tokens[F, String])
      .through(filter(root.field("trafficMeasurements").iterate.compile))
      .through(deserialize[F, TrafficMeasurements])

  def run[F[_]: Files: Sync](
      fileName: String,
      from: Intersection,
      to: Intersection
  ): F[String] = {
    for {
      trafficGrid <- TrafficGrid.make[F]
      _ <- trafficStream(fileName)
        .evalMap(trafficMeasurements =>
          trafficGrid.add(trafficMeasurements.measurements)
        )
        .compile
        .drain
      maybePath <- trafficGrid
        .findShortestPath(from, to)
        .map(_.getOrElse(None))
      jsonString = maybePath
        .map(TrafficPath.from)
        .getOrElse(TrafficPath.empty)
        .asJson
        .toString()
      _ = pprint.pprintln(jsonString)
    } yield jsonString
  }

  lazy val filePath = args(0)
  lazy val beginningAvenue = args(1)
  lazy val beginningStreet = args(2)
  lazy val endingAvenue = args(3)
  lazy val endingStreet = args(4)

  // since we must return a value, got to run the effect here
  run[IO](
    filePath,
    Intersection(Avenue(beginningAvenue), Street(beginningStreet)),
    Intersection(Avenue(endingAvenue), Street(endingStreet))
  ).unsafeRunSync()
}
