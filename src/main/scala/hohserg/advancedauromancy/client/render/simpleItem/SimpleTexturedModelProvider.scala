package hohserg.advancedauromancy.client.render.simpleItem

import hohserg.advancedauromancy.Main
import hohserg.advancedauromancy.client.ModelProvider
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item

trait SimpleTexturedModelProvider extends ModelProvider{
  def location: ModelResourceLocation = SimpleTexturedModelProvider.defaultLocation
  def textureName: String
}

object SimpleTexturedModelProvider {
  lazy val defaultLocation: ModelResourceLocation = new ModelResourceLocation(Main.advancedAuromancyModId+":simpletexturemodel", "inventory")
  object simpletexturemodel extends Item with SimpleTexturedModelProvider {
    override def textureName: String = "none"
    val name=getClass.getSimpleName.dropRight(1).toLowerCase
    setRegistryName(name)
    setUnlocalizedName(name)
  }

}
