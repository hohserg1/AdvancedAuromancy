package hohserg.advancedauromancy.recipes

import hohserg.advancedauromancy.core.Main
import hohserg.advancedauromancy.items.misc.{ThaumiumInnertCap, VoidInnertCap}
import hohserg.advancedauromancy.utils.ArcaneRecipeHelper._
import hohserg.advancedauromancy.utils.ItemUtils._
import hohserg.advancedauromancy.wands.AACaps._
import hohserg.advancedauromancy.wands.AARods._
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import thaumcraft.api.ThaumcraftApiHelper
import thaumcraft.api.aspects.Aspect
import thaumcraft.api.aspects.Aspect._
import thaumcraft.api.blocks.BlocksTC
import thaumcraft.api.items.ItemsTC

import scala.collection.JavaConverters._

@EventBusSubscriber(modid = Main.advancedAuromancyModId)
object RecipesInit {

  @SubscribeEvent
  def init(e: RegistryEvent.Register[IRecipe]): Unit = {
    //ThaumcraftApi.addFakeCraftingRecipe(BlockWandBuilder.getRegistryName, BlockWandBuilder)

    addArcaneCraftingRecipe("aa_wands@3", byItem(GreatwoodRod.item), 10,
      aspectList(ENTROPY -> 1, ORDER -> 1),
      Seq(
        Seq(ItemStack.EMPTY, byBlock(BlocksTC.logGreatwood)),
        Seq(byBlock(BlocksTC.logGreatwood), ItemStack.EMPTY)
      )
    )

    val goldNugget = byItem(Items.GOLD_NUGGET)
    val thaumiumNugget = byItem(ItemsTC.nuggets, 6)
    val voidNugget = byItem(ItemsTC.nuggets, 7)

    addReguralCapRecipe("aa_wands@3", byItem(GoldCap.item), goldNugget, 5)
    addReguralCapRecipe("aa_thaumium_cap", byItem(ThaumiumInnertCap), thaumiumNugget, 10)
    addReguralCapRecipe("aa_void_cap", byItem(VoidInnertCap), voidNugget, 25)

    addInfusionRecipe("aa_silverwood_rod", 3,
      byItem(SilverwoodRod.item),
      byBlock(BlocksTC.logSilverwood),
      Aspect.getPrimalAspects.asScala.map(ThaumcraftApiHelper.makeCrystal) :+ byItem(ItemsTC.salisMundus),
      aspectList(AIR -> 10, FIRE -> 10, EARTH -> 10, WATER -> 10, ENTROPY -> 10, ORDER -> 10, MAGIC -> 10))

    addInfusionRecipe("aa_thaumium_cap", 3,
      byItem(ThaumiumCap.item),
      byItem(ThaumiumInnertCap),
      Seq(byItem(ItemsTC.salisMundus, 0, 3)),
      aspectList(AURA -> 10, ENERGY -> 20))

    addInfusionRecipe("aa_void_cap", 3,
      byItem(VoidCap.item),
      byItem(VoidInnertCap),
      Seq(byItem(ItemsTC.salisMundus, 0, 4)),
      aspectList(AURA -> 20, ENERGY -> 20, ELDRITCH -> 20, VOID -> 20))
  }

  private def addReguralCapRecipe(research: String, result: ItemStack, nugget: ItemStack, vis: Int): Unit = {
    addArcaneCraftingRecipe(research, result, vis,
      aspectList(ORDER -> 1, FIRE -> 1, AIR -> 1),
      Seq(
        Seq(nugget, nugget, nugget),
        Seq(nugget, ItemStack.EMPTY, nugget)
      )
    )
  }
}
