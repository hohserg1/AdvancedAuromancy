package hohserg.advancedauromancy.api

import hohserg.advancedauromancy.wands.WandComponentRegistryEntry

case class WandCap(
                    discount: Int,
                    craftCost: Int
                  )
  extends WandComponentRegistryEntry[WandCap]
