package thaumcraft.common.lib

import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import thaumcraft.api.capabilities.ThaumcraftCapabilities
import thaumcraft.api.research.ResearchCategories
import thaumcraft.api.research.ResearchEntry.EnumResearchMeta
import thaumcraft.common.lib.research.ResearchManager

import scala.collection.JavaConverters._

object CommandThaumcraftAccess {

  def reset(player: EntityPlayer): Unit = {
    ThaumcraftCapabilities.getKnowledge(player).clear()
    ResearchCategories.researchCategories.values.asScala
      .flatMap(_.research.asScala.values.filter(_.hasMeta(EnumResearchMeta.AUTOUNLOCK)))
      .map(_.getKey)
      .foreach(ResearchManager.completeResearch(player, _, false))

    sync(player)
  }

  private def sync(player: EntityPlayer): Unit = {
    player match {
      case playerMP: EntityPlayerMP =>
        ThaumcraftCapabilities.getKnowledge(player).sync(playerMP)
      case _ =>
    }
  }

  def add(player: EntityPlayer, research: String): Unit ={
    CommandThaumcraft.giveRecursiveResearch(player, research)
    sync(player)
  }


}
