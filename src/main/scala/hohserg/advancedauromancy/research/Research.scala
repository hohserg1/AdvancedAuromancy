package hohserg.advancedauromancy.research

import hohserg.advancedauromancy.research.Icon.{ItemStackIcon, TextureIcon}
import hohserg.advancedauromancy.research.Status._
import net.minecraft.block.Block
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.ResourceLocation
import thaumcraft.api.research.ResearchEntry
import thaumcraft.api.research.ResearchEntry.EnumResearchMeta

case class Research[IconS <: Status, LocationS <: Status, StagesS <: Status] private(
                                                                                      private val name: String,
                                                                                      private val icons: Seq[Icon] = Seq(),
                                                                                      private val meta: Seq[EnumResearchMeta] = Seq(),
                                                                                      private val location: (Int, Int) = null,
                                                                                      private val stages: Seq[Stage] = Seq(),
                                                                                      private val parents: Seq[String] = Seq(),
                                                                                      private val rewardItems: Seq[ItemStack] = Seq()) {

  val fullName: String = "aa_" + name


  def icon(item: Icon, meta: EnumResearchMeta*): Research[Configured, LocationS, StagesS] = copy(icons = icons :+ item, meta = meta)

  def icon(item: ItemStack, meta: EnumResearchMeta*): Research[Configured, LocationS, StagesS] = icon(ItemStackIcon(item), meta: _*)

  def icon(item: ResourceLocation, meta: EnumResearchMeta*): Research[Configured, LocationS, StagesS] = icon(TextureIcon(item), meta: _*)

  def icon(item: Item, meta: EnumResearchMeta*): Research[Configured, LocationS, StagesS] = icon(new ItemStack(item), meta: _*)

  def icon(block: Block, meta: EnumResearchMeta*): Research[Configured, LocationS, StagesS] = icon(new ItemStack(block), meta: _*)

  def loc(x: Int, y: Int): Research[IconS, Configured, StagesS] = copy(location = (x, y))

  def parent(pname: String): Research[IconS, LocationS, StagesS] = copy(parents = parents :+ pname)

  def parent(p: Research[_, _, _]): Research[IconS, LocationS, StagesS] = copy(parents = parents :+ p.fullName)

  def stage(f: Stage => Stage): Research[IconS, LocationS, Configured] = copy(stages = stages :+ f(new Stage))


  def finish()(implicit eq: Research[IconS, LocationS, StagesS] =:= Research[Configured, Configured, Configured]): Research[Configured, Configured, Configured] = {
    registerResearch()
    this
  }

  private def toThaumcraft(implicit eq: Research[IconS, LocationS, StagesS] =:= Research[Configured, Configured, Configured]): ResearchEntry = {
    val r = new ResearchEntry()
    r.setKey(fullName)
    r.setName("research." + fullName + ".title")
    r.setCategory(ResearchInit.advancedAuromancyCategory.key)
    r.setDisplayColumn(location._1)
    r.setDisplayRow(location._2)
    r.setRewardItem(rewardItems.toArray)
    r.setParents(parents.toArray)
    r.setIcons(icons.map(_.value).toArray)
    r.setMeta(meta.toArray)
    r.setRewardItem(rewardItems.toArray)
    r.setStages(stages.zipWithIndex.map { case (v, index) => v.toThaumcraft(fullName, index) }.toArray)
    r
  }

  private def registerResearch()(implicit eq: Research[IconS, LocationS, StagesS] =:= Research[Configured, Configured, Configured]): Unit = {
    val ri = toThaumcraft

    val rl = ResearchInit.advancedAuromancyCategory

    rl.research.put(ri.getKey, ri)
    if (ri.getDisplayColumn < rl.minDisplayColumn)
      rl.minDisplayColumn = ri.getDisplayColumn

    if (ri.getDisplayRow < rl.minDisplayRow)
      rl.minDisplayRow = ri.getDisplayRow

    if (ri.getDisplayColumn > rl.maxDisplayColumn)
      rl.maxDisplayColumn = ri.getDisplayColumn

    if (ri.getDisplayRow > rl.maxDisplayRow)
      rl.maxDisplayRow = ri.getDisplayRow
  }


}

object Research {

  val research = Research.apply _

  def apply(name: String): Research[NotConfigured, NotConfigured, NotConfigured] = new Research(name)


}
