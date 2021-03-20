package hohserg.advancedauromancy.utils

import hohserg.advancedauromancy.core.Main
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.crafting.CraftingHelper
import thaumcraft.api.ThaumcraftApi
import thaumcraft.api.aspects.{Aspect, AspectList}
import thaumcraft.api.crafting.{CrucibleRecipe, InfusionRecipe, ShapedArcaneRecipe}

object ArcaneRecipeHelper {

  def aspectList(a: (Aspect, Int)*): AspectList = {
    val r = new AspectList()
    a.foreach { case (aspect, amount) => r.add(aspect, amount) }
    r
  }


  implicit val booleanNumeric: Numeric[Boolean] = new Numeric[Boolean] {
    override def plus(x: Boolean, y: Boolean): Boolean = x || y

    override def minus(x: Boolean, y: Boolean): Boolean = if (y) false else x

    override def times(x: Boolean, y: Boolean): Boolean = x && y

    override def negate(x: Boolean): Boolean = !x

    override def fromInt(x: Int): Boolean = x != 0

    override def toInt(x: Boolean): Int = if (x) 1 else 0

    override def toLong(x: Boolean): Long = if (x) 1 else 0

    override def toFloat(x: Boolean): Float = if (x) 1 else 0

    override def toDouble(x: Boolean): Double = if (x) 1 else 0

    override def compare(x: Boolean, y: Boolean): Int = 1
  }


  def makeRecipeRegistryName(result: ItemStack, craftingMatrix: Seq[Seq[ItemStack]]): ResourceLocation = {
    val itemName = result.getItem.getRegistryName

    new ResourceLocation(itemName.getResourceDomain, itemName.getResourcePath + "/@" + result.getItemDamage + "/x" + result.getCount + "/#" + craftingMatrix.hashCode())
  }

  def makeRecipeRegistryName(result: ItemStack, centralItem: ItemStack, items: Seq[ItemStack]): ResourceLocation = {
    val itemName = result.getItem.getRegistryName

    new ResourceLocation(itemName.getResourceDomain, itemName.getResourcePath + "/@" + result.getItemDamage + "/x" + result.getCount + "/#" + (items :+ centralItem).hashCode())
  }

  def addArcaneCraftingRecipe(researchKey: String, result: ItemStack, vis: Int, aspects: AspectList, craftingMatrix: Seq[Seq[ItemStack]], mirrored: Boolean = false): Unit = {

    val charMap = craftingMatrix.flatten.distinct.filter(!_.isEmpty).zipWithIndex.map { case (is, i) => is -> ('a' + i).toChar }.toMap.withDefaultValue(' ')

    val pattern: Seq[String] = craftingMatrix.map(_.map(charMap.apply).mkString)

    val charMap2 = charMap.flatMap { case (is, c) => Seq(Character.valueOf(c), is) }

    ThaumcraftApi.addArcaneCraftingRecipe(
      makeRecipeRegistryName(result, craftingMatrix),
      new ShapedArcaneRecipe(defaultGroup, researchKey, vis, aspects, result,
        CraftingHelper.parseShaped((Seq(Boolean.box(mirrored)) ++ pattern ++ charMap2): _*))
    )
  }

  def addInfusionRecipe(researchKey: String, instability: Int, result: ItemStack, centralItem: ItemStack, items: Seq[ItemStack], aspects: AspectList): Unit = {
    ThaumcraftApi.addInfusionCraftingRecipe(makeRecipeRegistryName(result, centralItem, items),
      new InfusionRecipe(researchKey, result, instability,
        aspects,
        centralItem,
        items: _*
      ))
  }

  private val defaultGroup = new ResourceLocation(Main.advancedAuromancyModId, "anyrecipe")

  def addCrucibleRecipe(researchKey: String, result: ItemStack, catalysts: ItemStack, aspects: AspectList): Unit = {
    ThaumcraftApi.addCrucibleRecipe(
      defaultGroup,
      new CrucibleRecipe(researchKey, result, catalysts, aspects)
    )
  }

}
