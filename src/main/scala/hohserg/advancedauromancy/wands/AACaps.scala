package hohserg.advancedauromancy.wands

import hohserg.advancedauromancy.api.WandCap

object AACaps {

  object GoldCap extends WandCap(30, 10)

  object ThaumiumCap extends WandCap(30, 30)

  object VoidCap extends WandCap(30, 50)

  object AuramCap extends WandCap(30, 100)

  object EnderCap extends WandCap(30, 100)

  object DefaultCap extends WandCap(30, 100) {
    override def isDefault = true
  }

  lazy val values: Seq[WandCap] = AllComponents.getAllComponentsFrom[this.type, WandCap](this)
}
