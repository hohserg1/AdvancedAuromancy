package hohserg.advancedauromancy.wands

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack


case class WandUpgrade(name: String, capacity: Int, discount: Float, craftCost: Int, onUpdate: (ItemStack, EntityPlayer) => Unit) extends WandComponentRegistryEntry[WandUpgrade]