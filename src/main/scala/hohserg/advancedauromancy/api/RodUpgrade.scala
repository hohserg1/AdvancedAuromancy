package hohserg.advancedauromancy.api

import hohserg.advancedauromancy.wands.WandComponentRegistryEntry
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

case class RodUpgrade(
                       additionCapacity: Int,
                       craftCost: Int,
                       onUpdate: (ItemStack, EntityPlayer) => Unit,
                       transformQuads: ItemStack => List[BakedQuad] => List[BakedQuad] = _ => identity
                     )
  extends WandComponentRegistryEntry[RodUpgrade]
