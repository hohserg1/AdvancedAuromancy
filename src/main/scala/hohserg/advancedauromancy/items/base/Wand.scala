package hohserg.advancedauromancy.items.base

import hohserg.advancedauromancy.items.base.Wand.{wandCapKey, wandRodKey, wandUpgradesKey}
import hohserg.advancedauromancy.nbt.Nbt
import hohserg.advancedauromancy.wands._
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagString
import net.minecraft.util.{ActionResult, EnumActionResult, EnumHand, ResourceLocation}
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.GameRegistry
import thaumcraft.api.items.IRechargable
import thaumcraft.common.config.ConfigItems
import thaumcraft.common.items.casters.{CasterManager, ItemCaster, ItemFocus}

import scala.collection.JavaConverters._

abstract class Wand(i: String) extends ItemCaster(i, 0) with IRechargable {
  def addVis(stack: ItemStack, amount: Float): Unit = setVis(stack, getVis(stack) + amount)

  ConfigItems.ITEM_VARIANT_HOLDERS.remove(this)

  def getUpgrades(is: ItemStack): List[WandUpgrade] =
    Nbt(is)
      .getTagList(wandUpgradesKey, 8)
      .iterator()
      .asScala
      .collect { case tag: NBTTagString => tag.getString }
      .map(new ResourceLocation(_))
      .map(GameRegistry.findRegistry(classOf[WandUpgrade]).getValue)
      .toList


  override def onUpdate(is: ItemStack, w: World, e: Entity, slot: Int, currentItem: Boolean): Unit = {
    e match {
      case player: EntityPlayer =>
        getRod(is).onUpdate(is, player)
        getUpgrades(is).foreach(_.onUpdate(is, player))
      case _ =>
    }
  }

  override def getConsumptionModifier(is: ItemStack, player: EntityPlayer, crafting: Boolean): Float = {
    val playerDiscount = if (player != null) CasterManager.getTotalVisDiscount(player) else 0

    val wandDiscount =
      (getCap(is).discount + (if (crafting) 0 else getUpgrades(is).map(_.additionDiscount(is, player, crafting)).sum)) / 100

    Math.max(1 - (wandDiscount + playerDiscount), 0.1F)
  }

  override def consumeVis(itemStack: ItemStack, entityPlayer: EntityPlayer, amount: Float, crafting: Boolean, sim: Boolean): Boolean = {
    val amount2 = amount * getConsumptionModifier(itemStack, entityPlayer, crafting)
    val current = getVis(itemStack)
    if (current >= amount2) {
      if (!sim)
        setVis(itemStack, current - amount2)
      true
    } else false
  }

  def getMaxVis(itemStack: ItemStack): Int = getRod(itemStack).capacity + getUpgrades(itemStack).map(_.additionCapacity).sum

  def setVis(itemStack: ItemStack, amount: Float): Unit

  def getVis(itemStack: ItemStack): Float

  def getFocusStackOption(stack: ItemStack): Option[ItemStack] = Option(getFocusStack(stack))


  def getFocusOption(stack: ItemStack): Option[ItemFocus] = Option(getFocus(stack))


  def getCap(itemStack: ItemStack): WandCap =
    GameRegistry.findRegistry(classOf[WandCap])
      .getValue(new ResourceLocation(Nbt(itemStack).getString(wandCapKey)))


  def getRod(itemStack: ItemStack): WandRod =
    GameRegistry.findRegistry(classOf[WandRod])
      .getValue(new ResourceLocation(Nbt(itemStack).getString(wandRodKey)))

  override def onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult[ItemStack] = {
    val r = super.onItemRightClick(world, player, hand)
    if (r.getType == EnumActionResult.SUCCESS)
      player.setSprinting(false)
    r
  }

  def getMaxCharge(itemStack: ItemStack, entityLivingBase: EntityLivingBase): Int =
    getMaxVis(itemStack)

  def showInHud(itemStack: ItemStack, entityLivingBase: EntityLivingBase) = IRechargable.EnumChargeDisplay.NEVER
}

object Wand {
  val wandUpgradesKey = "wandUpgrades"
  val wandCapKey = "cap"
  val wandRodKey = "rod"

}
