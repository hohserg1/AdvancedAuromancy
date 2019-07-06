package hohserg.advancedauromancy.client.render.simpleItem

import hohserg.advancedauromancy.client.ModelProvider
import hohserg.advancedauromancy.core.Main
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.ItemStack

trait SimpleTexturedModelProvider extends ModelProvider{
  def location: ModelResourceLocation = SimpleTexturedModelProvider.defaultLocation
  def textureName(itemStack: ItemStack): String
}

object SimpleTexturedModelProvider {
  lazy val defaultLocation: ModelResourceLocation = new ModelResourceLocation(Main.advancedAuromancyModId+":simpletexturemodel", "inventory")
}
