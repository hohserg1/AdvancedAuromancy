package hohserg.advancedauromancy.wands

import hohserg.advancedauromancy.client.render.Cube
import hohserg.advancedauromancy.core.Main.advancedAuromancyModId
import hohserg.advancedauromancy.items.ItemWandComponent
import hohserg.advancedauromancy.items.base.Wand
import hohserg.advancedauromancy.wands.WandRod.identityOnUpdate
import net.minecraft.client.Minecraft
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

  object DefaultRodUpgrade extends RodUpgrade("default_upgrade", 0, 0, identityOnUpdate)() {
    override def isDefault = true
  }

  object DefaultCapUpgrade extends CapUpgrade("default_upgrade", CapUpgrade.identityDiscount, 0, identityOnUpdate)() {
    override def isDefault = true
  }

  type ComponentByStack[A] = ItemStack => Option[A]

  def getByRegistry[A <: WandComponentRegistryEntry[A]](registry: IForgeRegistry[A]): ComponentByStack[A] =
    itemStack => Option(registry.getValue(ItemWandComponent.getComponentKey(itemStack))).filter(!_.isDefault)

  lazy val capByStack: ComponentByStack[WandCap] = getByRegistry(GameRegistry.findRegistry(classOf[WandCap]))
  lazy val rodByStack: ComponentByStack[WandRod] = getByRegistry(GameRegistry.findRegistry(classOf[WandRod]))
  lazy val rodUpgradeByStack: ComponentByStack[RodUpgrade] = getByRegistry(GameRegistry.findRegistry(classOf[RodUpgrade]))
  lazy val capUpgradeByStack: ComponentByStack[CapUpgrade] = getByRegistry(GameRegistry.findRegistry(classOf[CapUpgrade]))

  object GoldCap extends WandCap("gold_cap", 30, 100)()

  object ThaumiumCap extends WandCap("thaumium_cap", 30, 100)()

  object VoidCap extends WandCap("void_cap", 30, 100)()

  object AuramCap extends WandCap("auram_cap", 30, 100)()

  object EnderCap extends WandCap("ender_cap", 30, 100)()

  object GreatwoodRod extends WandRod("greatwood_rod", 100, 0, identityOnUpdate)()

  object SilverwoodRod extends WandRod("silverwood_rod", 100, 0, identityOnUpdate)()

  object TaintwoodRod extends WandRod("taintwood_rod", 100, 0, identityOnUpdate)()

  object BirchRod extends WandRod("birch_rod", 100, 0, identityOnUpdate)()

  object OakRod extends WandRod("oak_rod", 100, 0, identityOnUpdate)()

  object SpruceRod extends WandRod("spruce_rod", 100, 0, identityOnUpdate)()

  object JungleRod extends WandRod("jungle_rod", 100, 0, identityOnUpdate)()

  object ChargeIndicator extends RodUpgrade("charge_indicator", 0, 10, identityOnUpdate,
    stack => quads => {
      val (charge, maxCharge) = Wand.wand[(Float, Int)](stack)({ wand => wand.getVis(stack) -> wand.getMaxVis(stack) }, 0f -> 1)

      val textureAtlasSprite = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(advancedAuromancyModId + ":rods_and_caps/wand_charge_indicator")
      quads ++
        Cube(-1.1f, 1, 0.1f, 1.1f, 14 * (charge / maxCharge.toFloat), 1.1f, textureAtlasSprite, applyDiffuseLighting = false).scale(0.2f).move(0, 2, 0).toQuads
    })()

}
