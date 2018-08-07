package hohserg.advancedauromancy.items.base

import hohserg.advancedauromancy.nbt.Nbt._
import hohserg.advancedauromancy.wands._
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import thaumcraft.api.items.IRechargable
import thaumcraft.common.config.ConfigItems
import thaumcraft.common.items.casters.{CasterManager, ItemCaster, ItemFocus}

abstract class Wand(i:String) extends ItemCaster(i,0) with IRechargable {
  ConfigItems.ITEM_VARIANT_HOLDERS.remove(this)

  def getUpgrades(is: ItemStack):List[WandUpgrade] = is.getTagCompound.getList("upgrades").flatMap(WandUpgrade.apply(_))


  override def onUpdate(is: ItemStack, w: World, e: Entity, slot: Int, currentItem: Boolean): Unit = {

  }
  override def getConsumptionModifier(is: ItemStack, player: EntityPlayer, crafting: Boolean): Float = {
    var consumptionModifier = getCap(is).discount*getUpgrades(is).map(_.discount).product
    if (player != null) consumptionModifier -= CasterManager.getTotalVisDiscount(player)
    Math.max(consumptionModifier, 0.1F)
  }
  override def consumeVis(itemStack: ItemStack, entityPlayer: EntityPlayer, amount: Float, crafting: Boolean,sim: Boolean): Boolean = {
    val amount2 = amount * getConsumptionModifier(itemStack, entityPlayer, crafting)
    val current = getVis(itemStack)
    if (current >= amount2) {
      if(!sim)
        setVis(itemStack, current - amount2)
      true
    } else false
  }
  def getMaxVis(itemStack: ItemStack): Int = getRod(itemStack).capacity+getUpgrades(itemStack).map(_.capacity).sum

  def setVis(itemStack: ItemStack, amount: Float): Unit

  def getVis(itemStack: ItemStack): Float

  def getFocusStackOption(stack: ItemStack): Option[ItemStack] = Option(getFocusStack(stack))


  def getFocusOption(stack: ItemStack): Option[ItemFocus] = Option(getFocus(stack))


  def getCap(itemStack: ItemStack): WandCap = itemStack.getString("cap").flatMap(WandCap.apply).getOrElse(DefaultCap)

  def getRod(itemStack: ItemStack): WandRod = itemStack.getString("cap").flatMap(WandRod.apply).getOrElse(DefaultRod)

  def getMaxCharge(itemStack: ItemStack, entityLivingBase: EntityLivingBase): Int = getMaxVis(itemStack) * 1000

  def showInHud(itemStack: ItemStack, entityLivingBase: EntityLivingBase) = IRechargable.EnumChargeDisplay.NEVER
}
