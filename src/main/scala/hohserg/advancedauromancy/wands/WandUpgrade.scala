package hohserg.advancedauromancy.wands

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.item.ItemStack

abstract class WandUpgrade[A <: WandComponentRegistryEntry[A]] extends WandComponentRegistryEntry[A]{
  def transformQuads: ItemStack => List[BakedQuad] => List[BakedQuad]

}
