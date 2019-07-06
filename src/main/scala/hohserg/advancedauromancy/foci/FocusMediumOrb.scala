package hohserg.advancedauromancy.foci

import hohserg.advancedauromancy.core.Main
import hohserg.advancedauromancy.entities.EntityOrb
import thaumcraft.api.aspects.Aspect
import thaumcraft.api.casters.FocusNode.EnumSupplyType._
import thaumcraft.api.casters.{FocusMedium, NodeSetting}

class FocusMediumOrb extends FocusMedium{

  import thaumcraft.api.casters.Trajectory

  override def execute(trajectory: Trajectory): Boolean = {
    val speed = getSettingValue("speed") / 3.0F
    val p = getRemainingPackage
    if (p.getCaster != null) {
      val projectile = new EntityOrb(p, speed, trajectory, getSettingValue("option"))
      getPackage.getCaster.world.spawnEntity(projectile)
    }else
      false
  }

  override def createSettings: Array[NodeSetting] = {
    val option = Array(0, 1, 2)
    val optionDesc = Array("focus.common.none", "focus.orb.linear", "focus.orb.discharge")
    Array(new NodeSetting("option", "focus.common.options", new NodeSetting.NodeSettingIntList(option, optionDesc), "FOCUSPROJECTILE"), new NodeSetting("speed", "focus.projectile.speed", new NodeSetting.NodeSettingIntRange(1, 10)))
  }

  override def willSupply = Array(TARGET, TRAJECTORY)

  override def getComplexity: Int = 4 + (getSettingValue("speed") - 1) / 2

  override def getAspect: Aspect = Aspect.ORDER

  override def getKey: String = Main.advancedAuromancyModId+":ORB"

  override def getResearch: String = "FOCUSPROJECTILE@2"
}
