package hohserg.advancedauromancy.endervisnet

import java.io._

import hohserg.advancedauromancy.items.ItemEnderWandCasting
import hohserg.advancedauromancy.nbt.Nbt
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

import scala.collection.mutable
import scala.util.Try


object EnderVisNet {
  private val nets = new mutable.OpenHashMap[String, EVNEntry]()

  def loadOrCreate(name: String): EVNEntry = load(name).getOrElse(new EVNEntry(name, 0, 0))

  def load(name: String): Option[EVNEntry] = {
    Try {
      new File(evnSavePath).mkdirs()
      val oos = new ObjectInputStream(new FileInputStream(evnSavePath + name))
      val r = oos.readObject().asInstanceOf[EVNEntry]
      oos.close()
      r
    }.toOption
  }

  def getVisNetByStack(itemStack: ItemStack): Option[EVNEntry] = getName(itemStack).map(getVisNet)

  def getVisNet(name: String): EVNEntry = nets.getOrElseUpdate(name, loadOrCreate(name))


  def getName(itemStack: ItemStack): Option[String] =
    Option(Nbt(itemStack).getString(ItemEnderWandCasting.enderKeyTag)).filter(_.nonEmpty)


  private val evnSavePath = "./enderVisNet/"

  lazy val eventHandler = new EventHandler

  class EventHandler {
    @SubscribeEvent
    def onSave(e: WorldEvent.Save): Unit = {
      if (!e.getWorld.isRemote)
        try {
          new File(evnSavePath).mkdirs()
          nets.filter(!_._2.saved).foreach { i =>
            val oos = new ObjectOutputStream(new FileOutputStream(evnSavePath + i._1))
            oos.writeObject(i._2)
            oos.close()
          }
        } catch {
          case e: Exception => e.printStackTrace()
        }
    }

    @SubscribeEvent
    def onPlayerEnter(e: EntityJoinWorldEvent): Unit = {
      if (!e.getWorld.isRemote)
        e.getEntity match {
          case player: EntityPlayer =>
            val stacks = 0 until player.inventory.getSizeInventory map player.inventory.getStackInSlot
            val wandStacks = stacks filter (_.getItem == ItemEnderWandCasting)
            val evn = wandStacks flatMap getVisNetByStack
            evn foreach (_.updateClient())
          case _ =>
        }

    }
  }

}
