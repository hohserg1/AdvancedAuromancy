package hohserg.advancedauromancy.endervisnet

import hohserg.advancedauromancy.Packet
import hohserg.advancedauromancy.items.ItemEnderWandCasting.{getConsumptionModifier, getMaxVis}
import hohserg.advancedauromancy.nbt.Nbt._
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

import scala.collection.mutable

class ClientEnderVisNet extends EnderVisNet{
  override def updateClient(name: String): Unit = ()
}

class ServerEnderVisNet extends EnderVisNet{
  override def updateClient(name: String): Unit = {
    nets.get(name).foreach(data=>
      Packet.createPacket(Packet.UpdateVisAmount).writeString(name).writeFloat(data.charge).writeInt(data.maxCharge).sendToClients()
    )
  }
}

trait EnderVisNet {
  protected val nets = new mutable.OpenHashMap[String,Data]()

  def updateClient(name: String):Unit

  def setVis(name: String, amount:Float, maxAmount:Int):Unit={
    val data = nets.getOrElse(name, Data(0, maxAmount))

    val newMaxAmount = math.max(maxAmount, data.maxCharge)

    val newData = data.copy(charge = math.min(data.charge+amount, newMaxAmount), maxCharge = newMaxAmount)

    nets += name -> newData
    updateClient(name)
  }

  def setVis(itemStack: ItemStack, amount:Float):Unit =
    getName(itemStack).foreach(setVis(_,amount,getMaxVis(itemStack)))


  def getVis(name: String):Float =
    nets.get(name).map(_.charge).getOrElse(0)


  def getVis(itemStack: ItemStack):Float =
    getName(itemStack)
      .map(getVis)
      .getOrElse(0)



  def getName(itemStack: ItemStack): Option[String] =
    itemStack.getString("enderKey")

  def consumeVis(itemStack: ItemStack, entityPlayer: EntityPlayer, amount: Float, crafting: Boolean): Boolean = {
    val amount2=amount*getConsumptionModifier(itemStack, entityPlayer, crafting)
    val current=getVis(itemStack)
    if(current>=amount2){
      setVis(itemStack,current-amount2)
      true
    }else
      false

  }

}

case class Data(charge:Float,maxCharge:Int) extends Serializable
