package hohserg.advancedauromancy.items

import java.util

import hohserg.advancedauromancy.client.ClientEventHandler
import hohserg.advancedauromancy.client.render.simpleItem.SimpleTexturedModelProvider
import hohserg.advancedauromancy.core.Main
import hohserg.advancedauromancy.nbt.Nbt
import hohserg.advancedauromancy.wands.{WandCap, WandComponentRegistryEntry, WandRod, WandUpgrade}
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.{NonNullList, ResourceLocation}
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.registries.IForgeRegistry

import scala.collection.JavaConverters._

object ItemWandComponent extends Item with SimpleTexturedModelProvider {
  def loadTexturesFor[A <: WandComponentRegistryEntry[A]](getRegistry: IForgeRegistry[A]): Unit =
    getRegistry
      .getKeys
      .asScala
      .view
      .map(textureName)
      .map(new ResourceLocation(_))
      .foreach(ClientEventHandler.registerTexture)


  def getComponentKey(itemStack: ItemStack): ResourceLocation =
    new ResourceLocation(Nbt(itemStack).getString(componentNameTag))

  private val componentNameTag = "componentName"

  override def textureName(itemStack: ItemStack): String =
    textureName(getComponentKey(itemStack))

  private def textureName(resourceLocation: ResourceLocation) =
    resourceLocation.getResourceDomain + ":items/" + resourceLocation.getResourcePath

  override def getSubItems(tab: CreativeTabs, items: NonNullList[ItemStack]): Unit = {
    if (tab == Main.proxy.tab) {
      (GameRegistry.findRegistry(classOf[WandCap]).getKeys.asScala ++
        GameRegistry.findRegistry(classOf[WandRod]).getKeys.asScala ++
        GameRegistry.findRegistry(classOf[WandUpgrade]).getKeys.asScala
        ).foreach(key => {
        val stack = new ItemStack(this)
        Nbt(stack).setString(componentNameTag, key.toString)
        items.add(stack)
      })
    }
  }

  override def addInformation(stack: ItemStack, worldIn: World, tooltip: util.List[String], flagIn: ITooltipFlag): Unit = {
    super.addInformation(stack, worldIn, tooltip, flagIn)
    tooltip.add(getComponentKey(stack).toString)
  }
}