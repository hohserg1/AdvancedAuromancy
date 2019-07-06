package hohserg.advancedauromancy.endervisnet

import java.io._

import hohserg.advancedauromancy.network.Packet
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

import scala.util.Try

class ServerEnderVisNet extends EnderVisNet {

  override def updateClient(name: String): Unit = {
    nets.get(name).foreach(data =>
      Packet
        .createPacket(Packet.UpdateVisAmount)
        .writeString(name)
        .writeFloat(data.amount)
        .writeInt(data.maxAmount)
        .sendToClients()
    )
  }

  override def loadOrCreate(name: String): EVNEntry = load(name).getOrElse(EVNEntry.empty)

  def load(name: String): Option[EVNEntry] = {
    Try {
      new File(evnSavePath).mkdirs()
      val oos = new ObjectInputStream(new FileInputStream(evnSavePath + name))
      val r = oos.readObject().asInstanceOf[EVNEntry]
      oos.close()
      r
    }.toOption
  }


  private val evnSavePath = "./enderVisNet/"

  private var saved = false

  @SubscribeEvent
  def onSave(e: WorldEvent.Save): Unit = {
    println(Thread.currentThread())
    if (!saved)
      try {
        new File(evnSavePath).mkdirs()
        for (i <- nets) {
          val oos = new ObjectOutputStream(new FileOutputStream(evnSavePath + i._1))
          oos.writeObject(i._2)
          oos.close()
        }
        saved = true
      } catch {
        case e: Exception => e.printStackTrace()
      }
  }
}
