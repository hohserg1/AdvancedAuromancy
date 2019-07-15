package hohserg.advancedauromancy.wands

import hohserg.advancedauromancy.core.Main.advancedAuromancyModId
import hohserg.advancedauromancy.items.ItemWandComponent
import hohserg.advancedauromancy.wands.WandRod.identityOnUpdate
import hohserg.advancedauromancy.wands.WandUpgrade.identityDiscount
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.registries.IForgeRegistry

object RodsAndCaps {

  object DefaultRod extends WandRod("default_rod", 100, 0, identityOnUpdate)(new ResourceLocation(advancedAuromancyModId + ":rods_and_caps/wand_silverwood_rod")) {
    override def isDefault = true
  }

  object DefaultCap extends WandCap("default_cap", 30, 100)(new ResourceLocation(advancedAuromancyModId + ":rods_and_caps/wand_thaumium_cap")) {
    override def isDefault = true
  }

  object DefaultUpgrade extends WandUpgrade("default_upgrade", 0, identityDiscount, 0, identityOnUpdate) {
    override def isDefault = true
  }

  type ComponentByStack[A] = ItemStack => Option[A]

  def getByRegistry[A <: WandComponentRegistryEntry[A]](registry: IForgeRegistry[A]): ComponentByStack[A] =
    itemStack => Option(registry.getValue(ItemWandComponent.getComponentKey(itemStack))).filter(!_.isDefault)

  lazy val capByStack: ComponentByStack[WandCap] = getByRegistry(GameRegistry.findRegistry(classOf[WandCap]))
  lazy val rodByStack: ComponentByStack[WandRod] = getByRegistry(GameRegistry.findRegistry(classOf[WandRod]))
  lazy val upgradeByStack: ComponentByStack[WandUpgrade] = getByRegistry(GameRegistry.findRegistry(classOf[WandUpgrade]))
}
