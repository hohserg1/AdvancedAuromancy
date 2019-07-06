package hohserg.advancedauromancy.items

import hohserg.advancedauromancy.client.ModelProvider
import hohserg.advancedauromancy.core.Main
import hohserg.advancedauromancy.items.base.Wand
import hohserg.advancedauromancy.nbt.Nbt
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World

object ItemEnderWandCasting extends Wand("ItemEnderWandCasting".toLowerCase) with ModelProvider {
  val enderKeyTag = "enderKey"

  override def onUpdate(is: ItemStack, w: World, e: Entity, slot: Int, currentItem: Boolean): Unit = {
    e match {
      case e: EntityPlayer =>
        val nbt = Nbt(is)
        if (!nbt.hasKey(enderKeyTag))
          nbt.setString(enderKeyTag, e.getName)

      case _ =>
    }

  }

  override def setVis(itemStack: ItemStack, amount: Float): Unit = Main.proxy.enderVisNet.setVis(itemStack, amount)

  override def getVis(itemStack: ItemStack): Float = Main.proxy.enderVisNet.getVis(itemStack)

  override lazy val location: ModelResourceLocation = ItemWandCasting.location
}
