package hohserg.advancedauromancy.utils.endothermic.example.scala

import java.util

import com.google.common.collect.Lists
import hohserg.advancedauromancy.utils.endothermic.quad.{immutable, mutable}
import net.minecraft.client.renderer.block.model.BakedQuad

class MutableImmutableExample {
  def immutableVersion(quad: BakedQuad): util.List[BakedQuad] = {
    val q1 = immutable.LazyUnpackedQuad.apply(quad)
    val translated = q1.translate(0.1f, 0, 1)
    val rotated = q1.rotate(Math.PI.toFloat, 0, 1, 0)
    //q1 != translated; q1 != rotated ; translated != rotated
    Lists.newArrayList(translated.toBakedQuad, rotated.toBakedQuad)
  }

  def mutableVersion(quad: BakedQuad): util.List[BakedQuad] = {
    val q1 = mutable.LazyUnpackedQuad.apply(quad)
    val translated = q1.copy.translate(0.1f, 0, 1) //copy for save original version
    // q1 != translated

    val rotated = q1.rotate(Math.PI.toFloat, 0, 1, 0) //no copy, no new instance creating
    // q1 == rotated
    Lists.newArrayList(translated.toBakedQuad, rotated.toBakedQuad)
  }
}
