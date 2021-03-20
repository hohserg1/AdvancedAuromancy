package hohserg.advancedauromancy.client.render.wand

import net.minecraft.item.ItemStack

import scala.language.dynamics

case class ModelEntry(quads: QuadEntry, private val subentries: Map[String, ModelEntry]) extends Dynamic {
  def selectDynamic(name: String): ModelEntry = subentries(name)

  def +(name: String, v: ModelEntry): ModelEntry = copy(subentries = subentries + (name -> v))

  def +(name: String, v: Quad): ModelEntry = copy(quads = quads + (name, v))
}

object ModelEntry extends App {

  val empty = ModelEntry(QuadEntry(Map()), Map())

  def withQuads(q: (String, Quad)*): ModelEntry =
    ModelEntry(QuadEntry(Map(q: _*)), Map())

  val test = withQuads("q1" -> new Quad) + ("p1", withQuads("q1" -> new Quad))

  println(test.p1.quads.q1)
  println(test.quads.q1)

  def buildWandModel(is: ItemStack): ModelEntry = {
    val r= withQuads
  }

}
