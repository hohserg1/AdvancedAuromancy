package hohserg.advancedauromancy.wands

import hohserg.advancedauromancy.api._
import hohserg.advancedauromancy.api.capability.WandComponentCapabilityProvider.getWandComponent
import net.minecraft.item.ItemStack

object RodsAndCaps {
  type ComponentByStack[A] = ItemStack => Option[A]

  lazy val capByStack: ComponentByStack[WandCap] = getWandComponent
  lazy val rodByStack: ComponentByStack[WandRod] = getWandComponent
  lazy val rodUpgradeByStack: ComponentByStack[RodUpgrade] = getWandComponent
  lazy val capUpgradeByStack: ComponentByStack[CapUpgrade] = getWandComponent

}
