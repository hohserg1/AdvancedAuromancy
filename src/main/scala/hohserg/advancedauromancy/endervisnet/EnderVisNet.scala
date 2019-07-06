package hohserg.advancedauromancy.endervisnet

import hohserg.advancedauromancy.items.ItemEnderWandCasting._
import hohserg.advancedauromancy.nbt.Nbt
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

import scala.collection.mutable


trait EnderVisNet {
  protected val nets = new mutable.OpenHashMap[String, EVNEntry]()

  def updateClient(name: String): Unit

  def loadOrCreate(name: String): EVNEntry

  def getVisNet(name: String): EVNEntry = nets.getOrElseUpdate(name, loadOrCreate(name))


  def setVis(name: String, amount: Float, maxAmount: Int): Unit = {
    val data = nets.getOrElse(name, EVNEntry(0, maxAmount))

    val newMaxAmount = math.max(maxAmount, data.maxAmount)

    val newEVNEntry = data.copy(amount = math.min(data.amount + amount, newMaxAmount), maxAmount = newMaxAmount)

    nets += name -> newEVNEntry
    updateClient(name)
  }

  def setVis(itemStack: ItemStack, amount: Float): Unit =
    getName(itemStack).foreach(setVis(_, amount, getMaxVis(itemStack)))


  def getVis(name: String): Float =
    nets.get(name).map(_.amount).getOrElse(0)


  def getVis(itemStack: ItemStack): Float =
    getName(itemStack)
      .map(getVis)
      .getOrElse(0)


  def getName(itemStack: ItemStack): Option[String] =
    Option(Nbt(itemStack).getString(enderKeyTag)).filter(_.nonEmpty)

  def consumeVis(itemStack: ItemStack, entityPlayer: EntityPlayer, amount: Float, crafting: Boolean): Boolean = {
    val amount2 = amount * getConsumptionModifier(itemStack, entityPlayer, crafting)
    val current = getVis(itemStack)
    if (current >= amount2) {
      setVis(itemStack, current - amount2)
      true
    } else
      false

  }

}
