package hohserg.advancedauromancy.api

import hohserg.advancedauromancy.wands.WandComponentRegistryEntry
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

case class WandRod(
                    capacity: Int,
                    craftCost: Int,
                    onUpdate: (ItemStack, EntityPlayer) => Unit
                  )
  extends WandComponentRegistryEntry[WandRod]

object WandRod {
  val identityOnUpdate: (ItemStack, EntityPlayer) => Unit = (_, _) => ()
}