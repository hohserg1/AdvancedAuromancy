package hohserg.advancedauromancy.items

import hohserg.advancedauromancy.client.render.simpleItem.SimpleTexturedModelProvider
import hohserg.advancedauromancy.core.Main
import hohserg.advancedauromancy.nbt.Nbt
import hohserg.advancedauromancy.wands.WandCap
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.{NonNullList, ResourceLocation}
import net.minecraftforge.fml.common.registry.GameRegistry

import scala.collection.JavaConverters._

object ItemWandComponent extends Item with SimpleTexturedModelProvider {

  def getComponentKey(itemStack: ItemStack): ResourceLocation =
    new ResourceLocation(Nbt(itemStack).getString(componentNameTag))

  private val componentNameTag = "componentName"

  override def textureName(itemStack: ItemStack): String =
    Main.advancedAuromancyModId + ":items/" + getComponentKey(itemStack).getResourcePath

  override def getSubItems(tab: CreativeTabs, items: NonNullList[ItemStack]): Unit = {
    if (tab == Main.proxy.tab)
      Option(GameRegistry.findRegistry(classOf[WandCap])).foreach(_.getKeys.asScala.foreach(key => {
        val stack = new ItemStack(this)
        Nbt(stack).setString(componentNameTag, key.toString)
        items.add(stack)
      }))
  }
}