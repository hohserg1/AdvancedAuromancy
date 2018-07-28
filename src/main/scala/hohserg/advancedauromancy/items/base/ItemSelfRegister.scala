package hohserg.advancedauromancy.items.base

import hohserg.advancedauromancy.Main
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item

abstract class ItemSelfRegister extends Item{
  def name:String
  setRegistryName(name)
  setUnlocalizedName(name)
  CreativeTabs.CREATIVE_TAB_ARRAY.find(ct => ct.getTabLabel == Main.advancedAuromancyModId).foreach(setCreativeTab)

}
