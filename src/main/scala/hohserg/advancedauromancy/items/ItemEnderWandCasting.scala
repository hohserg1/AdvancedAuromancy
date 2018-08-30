package hohserg.advancedauromancy.items

import hohserg.advancedauromancy.Main
import hohserg.advancedauromancy.client.ModelProvider
import hohserg.advancedauromancy.items.base.Wand
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.ItemStack

object ItemEnderWandCasting extends Wand("ItemEnderWandCasting".toLowerCase) with ModelProvider{
  override def setVis(itemStack: ItemStack, amount: Float): Unit = Main.proxy.enderVisNet.setVis(itemStack, amount)

  override def getVis(itemStack: ItemStack): Float = Main.proxy.enderVisNet.getVis(itemStack)

  override lazy val location: ModelResourceLocation = ItemWandCasting.location
}
