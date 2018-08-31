package hohserg.advancedauromancy.items

import hohserg.advancedauromancy.Main
import hohserg.advancedauromancy.client.ModelProvider
import hohserg.advancedauromancy.items.base.Wand
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World

object ItemEnderWandCasting extends Wand("ItemEnderWandCasting".toLowerCase) with ModelProvider{
  override def onUpdate(is: ItemStack, w: World, e: Entity, slot: Int, currentItem: Boolean): Unit = {
    e match {
      case e: EntityPlayer =>
        val nbt=is.getTagCompound
        if(!nbt.hasKey("enderKey")) {
          nbt.setString("enderKey", e.getName)
        }
      case _=>
    }

  }
  override def setVis(itemStack: ItemStack, amount: Float): Unit = Main.proxy.enderVisNet.setVis(itemStack, amount)

  override def getVis(itemStack: ItemStack): Float = Main.proxy.enderVisNet.getVis(itemStack)

  override lazy val location: ModelResourceLocation = ItemWandCasting.location
}
