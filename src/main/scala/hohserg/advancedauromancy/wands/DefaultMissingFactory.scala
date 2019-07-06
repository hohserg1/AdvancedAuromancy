package hohserg.advancedauromancy.wands

import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.IForgeRegistry.MissingFactory

class DefaultMissingFactory[A <: WandComponentRegistryEntry[A]](value: A) extends MissingFactory[A] {
  override def createMissing(key: ResourceLocation, isNetwork: Boolean): A = value
}
