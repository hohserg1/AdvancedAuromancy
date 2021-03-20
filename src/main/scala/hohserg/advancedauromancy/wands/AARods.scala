package hohserg.advancedauromancy.wands

import hohserg.advancedauromancy.api.WandRod
import hohserg.advancedauromancy.api.WandRod.identityOnUpdate

object AARods{

  object GreatwoodRod extends WandRod(100, 10, identityOnUpdate)

  object SilverwoodRod extends WandRod(100, 40, identityOnUpdate)

  object TaintwoodRod extends WandRod(100, 40, identityOnUpdate)

  object BirchRod extends WandRod(100, 5, identityOnUpdate)

  object OakRod extends WandRod(100, 5, identityOnUpdate)

  object SpruceRod extends WandRod(100, 5, identityOnUpdate)

  object JungleRod extends WandRod(100, 5, identityOnUpdate)


  object DefaultRod extends WandRod(100, 0, identityOnUpdate) {
    override def isDefault = true
  }
  lazy val values = AllComponents.getAllComponentsFrom[this.type, WandRod](this)

}
