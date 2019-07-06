package hohserg.advancedauromancy.wands

import hohserg.advancedauromancy.client.TextureRegister
import hohserg.advancedauromancy.core.Main.advancedAuromancyModId
import net.minecraft.util.ResourceLocation

case class WandCap(name: String, discount: Float, craftCost: Int)(override val location: ResourceLocation = new ResourceLocation(advancedAuromancyModId + ":rods_and_caps/wand_" + name)) extends WandComponentRegistryEntry[WandCap] with TextureRegister