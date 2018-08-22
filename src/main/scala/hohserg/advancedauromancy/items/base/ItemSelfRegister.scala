package hohserg.advancedauromancy.items.base

import hohserg.advancedauromancy.Main
import hohserg.advancedauromancy.client.ModelProvider
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side

abstract class ItemSelfRegister extends Item{
  def name:String
  setRegistryName(name)
  setUnlocalizedName(name)
  CreativeTabs.CREATIVE_TAB_ARRAY.find(ct => ct.getTabLabel == Main.advancedAuromancyModId).foreach(setCreativeTab)
  if(FMLCommonHandler.instance().getEffectiveSide==Side.CLIENT) {
    this match{
      case item: ModelProvider =>
        val model = item.location
        Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(this, 0, model)
      case item =>
        val model = new ModelResourceLocation(name, "inventory")
        Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(this, 0, model)

    }
  }

}
