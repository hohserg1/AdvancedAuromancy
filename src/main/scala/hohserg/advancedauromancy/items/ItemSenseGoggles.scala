package hohserg.advancedauromancy.items

import hohserg.advancedauromancy.Main
import net.minecraft.util.ResourceLocation
import thaumcraft.common.items.armor.ItemGoggles

object ItemSenseGoggles extends ItemGoggles{
  private val texField=classOf[ItemGoggles].getDeclaredField("tex")
  texField.setAccessible(true)
  texField.set(this,new ResourceLocation(Main.advancedAuromancyModId, "textures/items/goggles_bauble.png"))

}
