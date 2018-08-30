package hohserg.advancedauromancy.items

import hohserg.advancedauromancy.client.ModelProvider
import hohserg.advancedauromancy.items.base.Wand
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagInt
import thaumcraft.api.items.RechargeHelper

object ItemWandCasting extends Wand("itemwandcasting")  with ModelProvider{
  override def setVis(itemStack: ItemStack, amount: Float): Unit = {
    if (itemStack != null) {
      val amount2 = Math.min(getMaxVis(itemStack), amount)
      itemStack.setTagInfo("tc.charge", new NBTTagInt((amount2*1000).toInt))
    }
  }

  override def getVis(itemStack: ItemStack): Float = RechargeHelper.getCharge(itemStack).toFloat / 1000

  override lazy val location: ModelResourceLocation = new ModelResourceLocation(getRegistryName, "inventory")
}
