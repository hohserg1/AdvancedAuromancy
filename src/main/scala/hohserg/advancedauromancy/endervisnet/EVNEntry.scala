package hohserg.advancedauromancy.endervisnet

import hohserg.advancedauromancy.network.Packet
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side

import scala.math.{max, min}

class EVNEntry(val name: String, private var amount: Float, private var maxAmount: Int) extends Serializable {
  @transient private[endervisnet] var saved = false

  def setVis(newAmount: Float, newMaxAmount: Int): Unit = {
    maxAmount = max(maxAmount, newMaxAmount)
    amount = min(maxAmount, newAmount)
    if (FMLCommonHandler.instance().getEffectiveSide == Side.SERVER) {
      updateClient()
      saved = false
    }
  }

  def addVis(newAmount: Float, newMaxAmount: Int): Unit = setVis(amount + newAmount, newMaxAmount)

  def getVis: Float = amount

  def getMaxVis: Int = maxAmount

  def consumeVis(required: Float): Boolean =
    if (required <= amount) {
      setVis(amount - required, maxAmount)
      true
    } else
      false

  def updateClient(): Unit = {
    Packet
      .createPacket(Packet.UpdateVisAmount)
      .writeString(name)
      .writeFloat(amount)
      .writeInt(maxAmount)
      .sendToClients()
  }


}
