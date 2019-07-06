package hohserg.advancedauromancy.nbt

import net.minecraft.item.ItemStack
import net.minecraft.nbt._

object Nbt {

  def apply(itemStack: ItemStack): NBTTagCompound = {
    if (!itemStack.hasTagCompound)
      itemStack.setTagCompound(new NBTTagCompound)
    itemStack.getTagCompound
  }

  def buildTag(value: Any): NBTBase = {
    value match {
      case v: String => new NBTTagString(v)
      case v: Int => new NBTTagInt(v)
      case v: Float => new NBTTagFloat(v)
      case v: Double => new NBTTagDouble(v)
      case v: Long => new NBTTagLong(v)
      case v: Short => new NBTTagShort(v)
      case v: Byte => new NBTTagByte(v)
      case v: Seq[Int] if v.nonEmpty && v.head.isInstanceOf[Int] => new NBTTagIntArray(v.toArray)
      case v: Seq[Long] if v.nonEmpty && v.head.isInstanceOf[Long] => new NBTTagLongArray(v.toArray)
      case v: Seq[_] => fromIterable(v)
      case v: Map[String, Any] => fromMap(v)
      case nbt: NBTBase => nbt
    }
  }

  def fromIterable(iterable: Iterable[Any]): NBTTagList =
    iterable.foldLeft(new NBTTagList()) {
      case (nbt, elem) =>
        nbt.appendTag(buildTag(elem))
        nbt
    }

  def fromMap(map: Map[String, Any]): NBTTagCompound = {
    map.foldLeft(new NBTTagCompound) {
      case (nbt, (key, value)) =>
        nbt.setTag(key, buildTag(value))
        nbt
    }
  }

}
