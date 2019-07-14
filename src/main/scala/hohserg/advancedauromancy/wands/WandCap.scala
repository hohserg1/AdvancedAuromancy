package hohserg.advancedauromancy.wands

import hohserg.advancedauromancy.client.TextureRegister
import hohserg.advancedauromancy.core.Main.advancedAuromancyModId
import net.minecraft.util.ResourceLocation

case class WandCap(protected val _name: String, discount: Int, craftCost: Int)(override val location: ResourceLocation = new ResourceLocation(advancedAuromancyModId + ":rods_and_caps/wand_" + _name)) extends WandComponentRegistryEntry[WandCap] with TextureRegister