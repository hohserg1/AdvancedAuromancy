package hohserg.advancedauromancy.wands

import hohserg.advancedauromancy.client.TextureRegister
import hohserg.advancedauromancy.core.Main.advancedAuromancyModId
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation


case class RodUpgrade(
                        protected val _name: String,
                        additionCapacity: Int,
                        craftCost: Int,
                        onUpdate: (ItemStack, EntityPlayer) => Unit,
                        transformQuads: ItemStack => List[BakedQuad] => List[BakedQuad] = _ => identity)
                     (override val location: ResourceLocation = new ResourceLocation(advancedAuromancyModId + ":rods_and_caps/wand_" + _name)) extends WandUpgrade[RodUpgrade] with TextureRegister

object RodUpgrade {

}