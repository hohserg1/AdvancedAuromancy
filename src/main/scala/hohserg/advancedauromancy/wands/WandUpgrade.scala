package hohserg.advancedauromancy.wands

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack


case class WandUpgrade(protected val _name: String, additionCapacity: Int, additionDiscount: (ItemStack, EntityPlayer) => Float, craftCost: Int, onUpdate: (ItemStack, EntityPlayer) => Unit) extends WandComponentRegistryEntry[WandUpgrade]