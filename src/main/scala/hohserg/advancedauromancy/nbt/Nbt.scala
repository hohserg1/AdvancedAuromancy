package hohserg.advancedauromancy.nbt

import collection.JavaConverters._
import net.minecraft.item.ItemStack
import net.minecraft.nbt._


object Nbt {
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

    private def get[A](name:String,f:String=>A):Option[A] = if(tag.hasKey(name)) Some(f(name)) else None
    def getTag(name:String):Option[Nbt] = get(name,tag.getCompoundTag).map(new Nbt(_))
    def getInt(name:String):Option[Int] = get(name,tag.getInteger)
    def getString(name:String):Option[String] = get(name,tag.getString)
  }
  implicit def apply(tag: ItemStack): Nbt = new Nbt(tag)
  implicit def apply(tag: NBTTagCompound): Nbt = new Nbt(if(tag==null)new NBTTagCompound else tag)


  implicit def map2Nbt[T<:Any](map: Map[String,T]): NBTTagCompound =
    map.foldLeft(new NBTTagCompound){
      case (acc:NBTTagCompound,(key:String,value:T))=>
        acc.setTag(key,matchElem(value))
        acc
    }
  private def matchElem(e:Any):NBTBase=
    e match {
      case v:String => new NBTTagString(v)
      case v:Int => new NBTTagInt(v)
      case v:Float => new NBTTagFloat(v)
      case v:Long => new NBTTagLong(v)
      case v:Seq[_] => v.foldLeft(new NBTTagList()){
        case (acc,elem)=>
          acc.appendTag(matchElem(elem))
          acc
      }
      case v:Map[String,Any]=>map2Nbt(v)
      case nbt:NBTBase => nbt
      //TODO: other NbtBase
    }

}
