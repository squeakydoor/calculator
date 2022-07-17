package model

import scalax.collection.GraphEdge.{EdgeCopy, ExtendedKey, NodeProduct}
import scalax.collection.GraphPredef.OuterEdge
import scalax.collection.edge.WDiEdge

final case class Junction[+N](
    fromIntersection: N,
    toIntersection: N,
    dataPoints: Int,
    override val weight: Double
) extends WDiEdge[N](
      NodeProduct(fromIntersection, toIntersection),
      weight
    )
    with ExtendedKey[N]
    with EdgeCopy[Junction]
    with OuterEdge[N, Junction] {
  def keyAttributes = Seq(dataPoints)
  private def this(nodes: Product, weight: Double, dataPoints: Int) {
    this(
      nodes.productElement(0).asInstanceOf[N],
      nodes.productElement(1).asInstanceOf[N],
      dataPoints,
      weight
    )
  }

  override def copy[NN](newNodes: Product) =
    new Junction[NN](newNodes, weight, dataPoints)

}

object Junction {
  implicit final class ImplicitEdge[A <: Intersection](val e: WDiEdge[A])
      extends AnyVal {
    def ##(dataPoints: Int) =
      new Junction[A](e.source, e.target, dataPoints, e.weight)
  }
}
