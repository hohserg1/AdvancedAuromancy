package hohserg.advancedauromancy.wands

import net.minecraftforge.registries.IForgeRegistryEntry

abstract class WandComponentRegistryEntry[A <: WandComponentRegistryEntry[A]] extends IForgeRegistryEntry.Impl[A] {

  def name = getRegistryName.toString

  protected def _name: String

  setRegistryName(_name)

  def isDefault = false

  override def toString: String = getClass.getSimpleName + "(" + name + ")"

}
