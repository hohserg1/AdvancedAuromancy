package hohserg.advancedauromancy.client.render.wand

import scala.language.dynamics

case class QuadEntry(quads: Map[String, Quad]) extends Dynamic {
  def selectDynamic(name: String): Quad = quads(name)

  def +(name: String, v: Quad): QuadEntry = copy(quads + (name -> v))
}
