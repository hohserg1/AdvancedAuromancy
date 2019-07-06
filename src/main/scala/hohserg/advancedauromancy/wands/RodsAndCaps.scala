package hohserg.advancedauromancy.wands

import hohserg.advancedauromancy.core.Main.advancedAuromancyModId
import net.minecraft.util.ResourceLocation

object RodsAndCaps {

  object DefaultRod extends WandRod("defaultrod", 100, 0, WandRod.identityOnUpdate)(new ResourceLocation(advancedAuromancyModId + ":rods_and_caps/wand_rod_silverwood")) {
    override def isDefault = true
  }

  object DefaultCap extends WandCap("defaultcap", 0.7f, 100)(new ResourceLocation(advancedAuromancyModId + ":rods_and_caps/wand_cap_thaumium")) {
    override def isDefault = true
  }

  object DefaultUpgrade extends WandUpgrade("defaultupgrade", 0, 0, 0, WandRod.identityOnUpdate) {
    override def isDefault = true
  }

}
