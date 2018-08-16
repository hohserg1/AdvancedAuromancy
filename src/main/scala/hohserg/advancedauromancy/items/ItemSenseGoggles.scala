package hohserg.advancedauromancy.items

import hohserg.advancedauromancy.Main
import net.minecraft.util.ResourceLocation
import thaumcraft.common.items.armor.ItemGoggles

object ItemSenseGoggles extends ItemGoggles{
  private val tex=classOf[ItemGoggles].getDeclaredField("tex")
  tex.setAccessible(true)
  tex.set(this,new ResourceLocation(Main.advancedAuromancyModId, "textures/items/goggles_bauble.png"))

}
