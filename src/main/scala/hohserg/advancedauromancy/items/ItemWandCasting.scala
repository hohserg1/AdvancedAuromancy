package hohserg.advancedauromancy.items

import hohserg.advancedauromancy.client.ModelProvider
import hohserg.advancedauromancy.items.base.Wand
import hohserg.advancedauromancy.nbt.Nbt
import hohserg.advancedauromancy.wands.WandCap
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.{ActionResult, EnumHand}
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.GameRegistry

object ItemWandCasting extends Wand("itemwandcasting") with ModelProvider {

  private val chargeTag = "tc.charge"
  private val remainderTag = "tc.chargeFloat"

  override def setVis(itemStack: ItemStack, amount: Float): Unit = {
    val nbt = Nbt(itemStack)

    val amount2 = Math.min(getMaxVis(itemStack), amount)
    nbt.setInteger(chargeTag, amount2.toInt)

    val remainder = amount2 - amount2.toInt
    nbt.setFloat(remainderTag, remainder)
  }

  override def getVis(itemStack: ItemStack): Float = {
    val nbt = Nbt(itemStack)
    nbt.getInteger(chargeTag).toFloat + nbt.getFloat(remainderTag)
  }

  override lazy val location: ModelResourceLocation = new ModelResourceLocation(getRegistryName, "inventory")

  override def onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult[ItemStack] = {
    println(Thread.currentThread())
    println(GameRegistry.findRegistry(classOf[WandCap]).getValue(null))
    super.onItemRightClick(world, player, hand)
  }
}
