package hohserg.advancedauromancy.research

import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

sealed trait Icon {
  def value: AnyRef
}

object Icon {

  case class ItemStackIcon(value: ItemStack) extends Icon

  case class TextureIcon(value: ResourceLocation) extends Icon

}
