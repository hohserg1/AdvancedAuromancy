package hohserg.advancedauromancy.wands

import hohserg.advancedauromancy.core.Main.advancedAuromancyModId
import net.minecraft.util.ResourceLocation

object RodsAndCaps {

  object DefaultRod extends WandRod("default_rod", 100, 0, WandRod.identityOnUpdate)(new ResourceLocation(advancedAuromancyModId + ":rods_and_caps/wand_silverwood_rod")) {
    override def isDefault = true
  }

  object DefaultCap extends WandCap("default_cap", 0.7f, 100)(new ResourceLocation(advancedAuromancyModId + ":rods_and_caps/wand_thaumium_cap")) {
    override def isDefault = true
  }

  object DefaultUpgrade extends WandUpgrade("default_upgrade", 0, 0, 0, WandRod.identityOnUpdate) {
    override def isDefault = true
  }

}
