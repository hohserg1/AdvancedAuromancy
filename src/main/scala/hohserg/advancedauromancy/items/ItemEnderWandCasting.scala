package hohserg.advancedauromancy.items

import hohserg.advancedauromancy.client.ModelProvider
import hohserg.advancedauromancy.endervisnet.EnderVisNet
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

        val nbtVis = ItemWandCasting.getVis(is)
        addVis(is, nbtVis)
        ItemWandCasting.setVis(is, 0)

      case _ =>
    }

  }

  override def setVis(itemStack: ItemStack, amount: Float): Unit =
    EnderVisNet
      .getVisNetByStack(itemStack)
      .foreach(_.setVis(amount, getMaxVis(itemStack)))

  override def getVis(itemStack: ItemStack): Float =
    EnderVisNet
      .getVisNetByStack(itemStack)
      .map(_.getVis)
      .map(math.min(_, getMaxVis(itemStack)))
      .getOrElse(0)

  override lazy val location: ModelResourceLocation = ItemWandCasting.location
}
