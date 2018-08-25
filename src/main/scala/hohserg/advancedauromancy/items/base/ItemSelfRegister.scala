package hohserg.advancedauromancy.items.base

import hohserg.advancedauromancy.Main
import hohserg.advancedauromancy.client.ClientEventHandler
import hohserg.advancedauromancy.client.render.simpleItem.SimpleTexturedModelProvider
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation

abstract class ItemSelfRegister extends Item with SimpleTexturedModelProvider{
  def name:String

  setRegistryName(name)
  setUnlocalizedName(name)
  CreativeTabs.CREATIVE_TAB_ARRAY.find(ct => ct.getTabLabel == Main.advancedAuromancyModId).foreach(setCreativeTab)
  Main.proxy.markForRegisterClient(this)

  override val textureName: String = Main.advancedAuromancyModId+":items/"+name
  ClientEventHandler.registerTexture(new ResourceLocation(textureName))
}
