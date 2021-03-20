package hohserg.advancedauromancy.wands

import hohserg.advancedauromancy.api.WandRod.identityOnUpdate
import hohserg.advancedauromancy.api.{CapUpgrade, WandRod}
import hohserg.advancedauromancy.items.ItemWandCasting
import thaumcraft.api.aspects.Aspect
import thaumcraft.api.aspects.Aspect._
import thaumcraft.common.items.casters.ItemFocus

object AACapUpgrades {

  object EnderCapPlating extends CapUpgrade(CapUpgrade.identityDiscount, 100, identityOnUpdate)

  def elementalPlatingOf(aspect: Aspect): CapUpgrade = {
    CapUpgrade(
      (stack, player, crafting) => {
        if (crafting)
          0
        else
          ItemWandCasting.getFocusStackOption(stack)
            .map(ItemFocus.getPackage)
            .map(_.getFocusEffects.map(_.getAspect))
            .map(aspects =>
              5 * aspects.count(_ == aspect) / aspects.length
            ).getOrElse(0)
      }, 50, WandRod.identityOnUpdate
    ).setRegistryName("elemental_plating_" + aspect.getTag)
  }


  val AirPlating = elementalPlatingOf(AIR)
  val EarthPlating = elementalPlatingOf(EARTH)
  val EntropyPlating = elementalPlatingOf(ENTROPY)
  val OrderPlating = elementalPlatingOf(ORDER)
  val FirePlating = elementalPlatingOf(FIRE)
  val ColdPlating = elementalPlatingOf(COLD)


  object DefaultCapUpgrade extends CapUpgrade(CapUpgrade.identityDiscount, 0, identityOnUpdate) {
    override def isDefault = true
  }

  lazy val values = AllComponents.getAllComponentsFrom[this.type, CapUpgrade](this)

}
