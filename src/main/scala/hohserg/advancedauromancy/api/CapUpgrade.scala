package hohserg.advancedauromancy.api

import hohserg.advancedauromancy.wands.WandComponentRegistryEntry
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

case class CapUpgrade(
                       additionDiscount: (ItemStack, EntityPlayer, Boolean) => Int,
                       craftCost: Int,
                       onUpdate: (ItemStack, EntityPlayer) => Unit
                     )
  extends WandComponentRegistryEntry[CapUpgrade]

object CapUpgrade {
  val identityDiscount: (ItemStack, EntityPlayer, Boolean) => Int = (_, _, _) => 0
}
