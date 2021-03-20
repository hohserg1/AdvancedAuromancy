package hohserg.advancedauromancy.api

import hohserg.advancedauromancy.wands.WandComponentRegistryEntry

case class ScepterResonator(
                             discountModifier: Double,
                             craftCost: Int
                           )
  extends WandComponentRegistryEntry[ScepterResonator]
