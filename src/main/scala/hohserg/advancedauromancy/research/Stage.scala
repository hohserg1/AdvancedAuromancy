package hohserg.advancedauromancy.research

import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import thaumcraft.api.research.ResearchStage
import thaumcraft.api.research.ResearchStage.Knowledge
import thaumcraft.common.lib.research.ResearchManager

case class Stage(
                  private val requiredItems: Seq[ItemStack] = Seq(),
                  private val requiredCrafts: Seq[ItemStack] = Seq(),
                  private val requiredResearches: Seq[String] = Seq(),
                  private val requiredKnowledges: Seq[Knowledge] = Seq(),
                  private val recipes: Seq[ResourceLocation] = Seq(), warp: Int = 0) {

  def requiredItems(items: ItemStack*): Stage = copy(requiredItems = items)

  def requiredCrafts(items: ItemStack*): Stage = copy(requiredCrafts = items)

  def requiredResearch(pname: String): Stage = copy(requiredResearches = requiredResearches :+ pname)

  def requiredResearch(p: Research[_, _, _]): Stage = copy(requiredResearches = requiredResearches :+ p.fullName)

  def recipes(items: ResourceLocation*): Stage = copy(recipes = items)

  def requiredKnowledges(k: Knowledge*): Stage = copy(requiredKnowledges = k)

  def warp(w: Int): Stage = copy(warp = w)

  def toThaumcraft(researchName: String, index: Int): ResearchStage = {
    val r = new ResearchStage
    r.setText("research." + researchName + ".stage." + (index + 1))

    if (requiredItems.nonEmpty)
      r.setObtain(requiredItems.toArray)

    if (requiredCrafts.nonEmpty) {
      r.setCraft(requiredCrafts.toArray)
      r.setCraftReference(requiredCrafts.map(ResearchManager.createItemStackHash).toArray)
    }

    if (requiredResearches.nonEmpty) {
      val preparedResearches = requiredResearches.map(_.split(";")).map {
        case Array(research, icon) => research -> icon
        case Array(research) => research -> null
      }
      r.setResearch(preparedResearches.map(_._1).toArray)
      r.setResearchIcon(preparedResearches.map(_._2).toArray)
    }

    if (requiredKnowledges.nonEmpty)
      r.setKnow(requiredKnowledges.toArray)

    r.setRecipes(recipes.toArray)
    r.setWarp(warp)
    r
  }

}
