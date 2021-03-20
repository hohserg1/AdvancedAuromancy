package hohserg.advancedauromancy.wands

import hohserg.advancedauromancy.api.ScepterResonator
import net.minecraft.item.Item
import thaumcraft.api.items.ItemsTC

object AAResonators  {

  object VisResonator extends ScepterResonator(1.1, 5) {
    override def useRegularItemRepresent: Boolean = false
    override lazy val item: Item = ItemsTC.visResonator
  }

  object AdvancedResonator extends ScepterResonator(1.2, 15)

  object HonedResonator extends ScepterResonator(1.3, 15)

  object VoidResonator extends ScepterResonator(1.5, 15)

  lazy val values = AllComponents.getAllComponentsFrom[this.type, ScepterResonator](this)
}
