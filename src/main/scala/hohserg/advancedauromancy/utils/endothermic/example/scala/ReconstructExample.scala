package hohserg.advancedauromancy.utils.endothermic.example.scala

import hohserg.advancedauromancy.utils.endothermic.quad.immutable.LazyUnpackedQuad
import net.minecraft.client.renderer.block.model.BakedQuad

class ReconstructExample extends (BakedQuad => BakedQuad) {
  override def apply(v1: BakedQuad): BakedQuad =
    LazyUnpackedQuad(v1)
      .reconstruct(v1_x = 0.5f)
      .toBakedQuad
}
