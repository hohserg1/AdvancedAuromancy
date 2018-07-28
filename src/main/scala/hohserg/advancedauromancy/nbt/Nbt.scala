package hohserg.advancedauromancy.nbt

import collection.JavaConverters._
import net.minecraft.item.ItemStack
import net.minecraft.nbt._

class Nbt(val tag:NBTTagCompound) {
  def this(stack:ItemStack) {
    this(Option(stack.getTagCompound).getOrElse({
      stack.setTagCompound(new NBTTagCompound)
      stack.getTagCompound
    }))
  }
  def set(name:String,value:Nbt): Unit = tag.setTag(name,value.tag.copy())
  def set(name:String,value:String): Unit = tag.setString(name,value)
  def set(name:String,value:Int): Unit = tag.setInteger(name,value)

  def getList(name:String):List[String] = tag.getTagList(name, 8).iterator().asScala.map{case i:NBTTagString=>i.getString}.toList

  def getTag(name:String):Option[Nbt] = Option(tag.getCompoundTag(name)).map(new Nbt(_))
  def getInt(name:String):Option[Int] = Option(tag.getInteger(name))
  def getString(name:String):Option[String] = Option(tag.getString(name))
}

object Nbt {
  implicit def apply(tag: ItemStack): Nbt = new Nbt(tag)
  implicit def apply(tag: NBTTagCompound): Nbt = new Nbt(if(tag==null)new NBTTagCompound else tag)

}
