package hohserg.advancedauromancy.wands

import hohserg.advancedauromancy.client.TextureRegister
import hohserg.advancedauromancy.core.Main.advancedAuromancyModId
import hohserg.advancedauromancy.items.ItemWandComponent
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.IForgeRegistryEntry

abstract class WandComponentRegistryEntry[A <: WandComponentRegistryEntry[A]] extends IForgeRegistryEntry.Impl[A] with TextureRegister {
  this: A =>

  def name = getRegistryName.toString

  def useRegularItemRepresent: Boolean = !isDefault

  lazy val item: Item = new ItemWandComponent(this)

  def craftCost: Int

  def isDefault = false

  override def toString: String = getClass.getSimpleName //+ "(" + name + ")"

  override def location: ResourceLocation = new ResourceLocation(advancedAuromancyModId + ":rods_and_caps/wand_" + getRegistryName.getResourcePath)

  override def hashCode(): Int =
    name.hashCode

  override def equals(obj: Any): Boolean = {
    obj match {
      case o: WandComponentRegistryEntry[_] =>
        o.name == this.name
      case _ => false
    }
  }
}
