package hohserg.advancedauromancy.items

import hohserg.advancedauromancy.client.render.simpleItem.SimpleTexturedModelProvider
import hohserg.advancedauromancy.wands._
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation

class ItemWandComponent[A <: WandComponentRegistryEntry[A]](val component: A) extends Item with SimpleTexturedModelProvider {

  override def textureName: String =
    textureName(component.getRegistryName).toString

  private def textureName(resourceLocation: ResourceLocation): ResourceLocation =
    new ResourceLocation(resourceLocation.getResourceDomain, "items/" + resourceLocation.getResourcePath)
}