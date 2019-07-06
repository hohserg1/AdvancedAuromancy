package hohserg.advancedauromancy.wands

import hohserg.advancedauromancy.client.TextureRegister
import hohserg.advancedauromancy.core.Main.advancedAuromancyModId
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

case class WandRod(name: String, capacity: Int, craftCost: Int, onUpdate: (ItemStack, EntityPlayer) => Unit)(override val location: ResourceLocation = new ResourceLocation(advancedAuromancyModId + ":rods_and_caps/wand_" + name)) extends WandComponentRegistryEntry[WandRod] with TextureRegister

object WandRod {
  val identityOnUpdate: (ItemStack, EntityPlayer) => Unit = (_, _) => ()

}