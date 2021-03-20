package hohserg.advancedauromancy.research

import hohserg.advancedauromancy.blocks.BlockWandBuilder
import hohserg.advancedauromancy.core.Main.advancedAuromancyModId
import hohserg.advancedauromancy.research.Research._
import hohserg.advancedauromancy.utils.ItemUtils._
import hohserg.advancedauromancy.wands.AACaps.GoldCap
import hohserg.advancedauromancy.wands.AARods.{GreatwoodRod, SilverwoodRod}
import net.minecraft.util.ResourceLocation
import thaumcraft.Thaumcraft
import thaumcraft.api.ThaumcraftApiHelper.makeCrystal
import thaumcraft.api.aspects.Aspect.{AURA, CRAFT, MAGIC}
import thaumcraft.api.aspects.{Aspect, AspectList}
import thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType
import thaumcraft.api.items.ItemsTC
import thaumcraft.api.research.ResearchCategories
import thaumcraft.api.research.ResearchEntry.EnumResearchMeta._
import thaumcraft.api.research.ResearchStage.Knowledge

import scala.collection.JavaConverters._

object ResearchInit {


  lazy val advancedAuromancyCategory = ResearchCategories.registerCategory(advancedAuromancyModId, "FLUX",
    new AspectList().add(AURA, 1).add(CRAFT, 1).add(MAGIC, 1),
    new ResourceLocation(advancedAuromancyModId, "textures/icon.png"),
    new ResourceLocation(advancedAuromancyModId, "textures/background.png")
  )
  lazy val auromancyCatergory = ResearchCategories.getResearchCategory("AUROMANCY")

  def init(): Unit = {
    advancedAuromancyCategory.research.clear()

    val wands = research("wands")
      .icon(BlockWandBuilder, ROUND, HIDDEN)
      .loc(0, 0)
      .parent("BASEAUROMANCY")
      .parent("!PLANTWOOD")
      .stage(
        _.requiredItems(makeCrystal(Aspect.AURA), byItem(ItemsTC.visResonator), makeCrystal(Aspect.FLUX))
          .requiredResearch("BASEAUROMANCY")
          .requiredResearch("FLUX")
      )
      .stage(
        _.requiredCrafts(byBlock(BlockWandBuilder))
      )
      .stage(
        _
          .recipes(GreatwoodRod.getRegistryName)
          .recipes(GoldCap.getRegistryName)
      )
      .finish()

    val sliverwoodRod = research("rod_silverwood")
      .icon(byItem(SilverwoodRod.item))
      .loc(2, 2)
      .parent(wands)
      .stage(
        _.requiredKnowledges(new Knowledge(EnumKnowledgeType.THEORY, auromancyCatergory, 1))
          .recipes(SilverwoodRod.getRegistryName)
      )


    advancedAuromancyCategory.research.asScala.values.groupBy(r => r.getDisplayColumn -> r.getDisplayRow).filter(_._2.size >= 2)
      .foreach { case ((x, y), r) =>
        Thaumcraft.log.warn("Researches [" + r.mkString(", ") + "] overlaps location (" + x + ", " + y + ") at ")
      }

  }


}
