package hohserg.advancedauromancy

import java.io._

import net.minecraft.world.DimensionType
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

import scala.collection.generic.Growable
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object Savable{
  val openListeners=new ListBuffer[mutable.OpenHashMap[_, _] with OpenSavable[_, _]]
  def register[A,B](value: mutable.OpenHashMap[A, B] with OpenSavable[A, B]): Unit = openListeners+=value

  def preinit(event: FMLPreInitializationEvent): Unit = {
    MinecraftForge.EVENT_BUS.register(new WorldListener)
  }
  val listeners=new ListBuffer[Savable[_] with Serializable]
  def register[A](saveble: Savable[A] with Serializable): Unit = listeners+=saveble


  class WorldListener{

    @SubscribeEvent
    def onSave(e:WorldEvent.Save): Unit ={
      if(e.getWorld.provider.getDimensionType == DimensionType.OVERWORLD)
        try {
          new File("./saveble/").mkdirs()
          for (i <- listeners) {
            val oos = new ObjectOutputStream(new FileOutputStream("./saveble/" + i.name))
            oos.writeObject(i)
            oos.close()
          }
          for (i <- openListeners) {
            val oos = new ObjectOutputStream(new FileOutputStream("./saveble/" + i.name))
            oos.writeObject(i.toMap)
            oos.close()
          }
        }catch {
          case e:Exception=>e.printStackTrace()
        }
    }
  }
}

trait OpenSavable[A,B] {
  this:mutable.OpenHashMap[A,B]=>
  def name:String
  Savable.register(this)
  try{
    new File("./saveble/").mkdirs()
    val oos = new ObjectInputStream(new FileInputStream("./saveble/"+name))
    this++=oos.readObject().asInstanceOf[Map[A,B]]
    oos.close()
  }catch {
    case e:Exception=>e.printStackTrace()
  }
}

trait Savable[A] {
  this:Growable[A] with Serializable=>
  def name:String
  Savable.register(this)
  try{
    new File("./saveble/").mkdirs()
      val oos = new ObjectInputStream(new FileInputStream("./saveble/"+name))
      this++=oos.readObject().asInstanceOf[mutable.Traversable[A]]
      oos.close()
  }catch {
    case e:Exception=>e.printStackTrace()
  }
}
