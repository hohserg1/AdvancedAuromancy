package hohserg.advancedauromancy.utils

import hohserg.advancedauromancy.core.Main
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.registries.IForgeRegistryEntry
import thaumcraft.api.aspects.AspectList
import thaumcraft.api.crafting.{CrucibleRecipe, IArcaneRecipe}
import thaumcraft.api.items.ItemsTC
import thaumcraft.api.{ThaumcraftApi, ThaumcraftApiHelper}

object ArcaneRecipeHelper{
  implicit val booleanNumeric:Numeric[Boolean] = new Numeric[Boolean] {
    override def plus(x: Boolean, y: Boolean): Boolean = x || y

    override def minus(x: Boolean, y: Boolean): Boolean = if(y) false else x

    override def times(x: Boolean, y: Boolean): Boolean = x && y

    override def negate(x: Boolean): Boolean = !x

    override def fromInt(x: Int): Boolean = x != 0

    override def toInt(x: Boolean): Int = if(x) 1 else 0

    override def toLong(x: Boolean): Long = if(x) 1 else 0

    override def toFloat(x: Boolean): Float = if(x) 1 else 0

    override def toDouble(x: Boolean): Double = if(x) 1 else 0

    override def compare(x: Boolean, y: Boolean): Int = 1
  }

  def addArcaneCraftingRecipe(researchKey:String, result:ItemStack, vis:Int, aspects:AspectList, craftingMatrix:Seq[Seq[ItemStack]]): Unit ={
    ThaumcraftApi.addArcaneCraftingRecipe(
      defaultGroup,
      new IForgeRegistryEntry.Impl[IRecipe] with IArcaneRecipe {
        override def getVis: Int = vis

        override def getResearch: String = researchKey

        override def getCrystals: AspectList = aspects

        override def matches(inv: InventoryCrafting, worldIn: World): Boolean =
          if (inv.getSizeInventory < 15)
            false
          else {
            (for{
              aspect <- aspects.getAspects
              cs = ThaumcraftApiHelper.makeCrystal(aspect, aspects.getAmount(aspect))
            } yield
                (for{
                  i <- 0 to 5
                  itemstack1 = inv.getStackInSlot(9 + i)
                  exist = itemstack1 != null && itemstack1.getItem == ItemsTC.crystalEssence && itemstack1.getCount >= cs.getCount
                } yield exist).sum
              ).product &&
              (for {
                i <- 0 until inv.getWidth
                j <- 0 until inv.getHeight
                item1 = inv.getStackInRowAndColumn(i, j)
                item2 = craftingMatrix(i)(j)
              } yield
                  ThaumcraftApiHelper.areItemsEqual(item1, item2)).product

          }


        override def getCraftingResult(inv: InventoryCrafting): ItemStack = result

        override def canFit(width: Int, height: Int): Boolean = width == craftingMatrix.size

        override def getRecipeOutput: ItemStack = result
      })


  }

  private val defaultGroup = new ResourceLocation(Main.advancedAuromancyModId, "anyrecipe")

  def addCrucibleRecipe(researchKey:String, result:ItemStack, catalysts:ItemStack, aspects:AspectList): Unit ={
    ThaumcraftApi.addCrucibleRecipe(
      defaultGroup,
      new CrucibleRecipe(researchKey, result, catalysts, aspects)
    )
  }

}
