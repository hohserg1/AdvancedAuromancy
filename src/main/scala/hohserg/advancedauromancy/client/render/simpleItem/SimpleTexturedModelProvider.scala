package hohserg.advancedauromancy.client.render.simpleItem

import hohserg.advancedauromancy.client.ModelProvider
import hohserg.advancedauromancy.core.Main
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item

trait SimpleTexturedModelProvider extends ModelProvider {
  this: Item =>
  def location: ModelResourceLocation = SimpleTexturedModelProvider.defaultLocation

  def textureName: String = getRegistryName.getResourceDomain + ":items/" + getRegistryName.getResourcePath
}

object SimpleTexturedModelProvider {
  lazy val defaultLocation: ModelResourceLocation = new ModelResourceLocation(Main.advancedAuromancyModId + ":simpletexturemodel", "inventory")

  object simpletexturemodel extends Item with SimpleTexturedModelProvider {
    setRegistryName("simpletexturemodel")

    override def textureName: String = "none"
  }

}
