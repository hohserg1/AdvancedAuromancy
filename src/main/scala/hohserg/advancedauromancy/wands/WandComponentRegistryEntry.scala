package hohserg.advancedauromancy.wands

import net.minecraftforge.registries.IForgeRegistryEntry

abstract class WandComponentRegistryEntry[A<:WandComponentRegistryEntry[A]] extends IForgeRegistryEntry.Impl[A] {

  def name: String

  setRegistryName(name)

  def isDefault = false

}
