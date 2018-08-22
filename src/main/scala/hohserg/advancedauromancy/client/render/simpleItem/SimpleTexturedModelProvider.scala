package hohserg.advancedauromancy.client.render.simpleItem

import hohserg.advancedauromancy.client.ModelProvider
import net.minecraft.client.renderer.block.model.ModelResourceLocation

trait SimpleTexturedModelProvider extends ModelProvider{
  def location: ModelResourceLocation = new ModelResourceLocation("simpletexturemodel", "inventory")
}
