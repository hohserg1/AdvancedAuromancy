package hohserg.advancedauromancy.client.render.wand

import hohserg.advancedauromancy.utils.endothermic.quad.immutable.LazyUnpackedQuad

import scala.language.dynamics

case class QuadEntry(quads: Map[String, LazyUnpackedQuad]) extends Dynamic {
  def selectDynamic(name: String): LazyUnpackedQuad = quads(name)

  def +(name: String, v: LazyUnpackedQuad): QuadEntry = copy(quads + (name -> v))
}
