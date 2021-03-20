package thaumcraft.common.lib.crafting

import hohserg.advancedauromancy.blocks.BlockWandBuilder
import hohserg.advancedauromancy.utils.ItemUtils
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.FMLCommonHandler
import thaumcraft.api.ThaumcraftApiHelper
import thaumcraft.api.aspects.{Aspect, AspectHelper}
import thaumcraft.api.blocks.BlocksTC
import thaumcraft.api.capabilities.ThaumcraftCapabilities
import thaumcraft.api.crafting.IDustTrigger
import thaumcraft.api.research.ResearchCategories
import thaumcraft.common.container.InventoryFake
import thaumcraft.common.lib.research.ResearchManager
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench

import scala.collection.JavaConverters._

class WandBuilderCraft extends DustTriggerSimple("aa_wands@2", BlocksTC.arcaneWorkbench, new ItemStack(Item.getItemFromBlock(BlockWandBuilder))) {
  override def getValidFace(world: World, player: EntityPlayer, pos: BlockPos, face: EnumFacing): IDustTrigger.Placement = {
    val r = super.getValidFace(world, player, pos, face)
    if (world.isRemote)
      r
    else
      world.getTileEntity(pos) match {
        case workbench: TileArcaneWorkbench =>
          val woolCarpet = new ItemStack(Item.getItemFromBlock(Blocks.CARPET), 1, 11)
          val slabSilverwood = new ItemStack(Item.getItemFromBlock(BlocksTC.slabSilverwood))
          val plankSilverwood = new ItemStack(Item.getItemFromBlock(BlocksTC.plankSilverwood))

          val test =
            Vector(
              woolCarpet, woolCarpet, woolCarpet,
              slabSilverwood, slabSilverwood, slabSilverwood,
              plankSilverwood, ItemStack.EMPTY, plankSilverwood
            )

          val grid = 0 to 8 map workbench.inventoryCraft.getStackInSlot

          val crystals = 9 to 14 map workbench.inventoryCraft.getStackInSlot

          val recipeAreEquals = grid.zip(test).map((ItemStack.areItemsEqual _).tupled).forall(identity)

          val lists =
            crystals
              .map(AspectHelper.getObjectAspects)
              .map(Option.apply)
              .map(_.flatMap(_.getAspects.headOption))
              .toSet
          val a = Aspect.getPrimalAspects.asScala.map(Option.apply).toSet
          val crystalAreFound = lists == a
          if (recipeAreEquals && crystalAreFound)
            r
          else
            null

        case _ =>
          null
      }
  }

  override def execute(world: World, player: EntityPlayer, pos: BlockPos, placement: IDustTrigger.Placement, side: EnumFacing): Unit = {
    world.getTileEntity(pos) match {
      case workbench: TileArcaneWorkbench =>
        0 to 14 map (workbench.inventoryCraft.decrStackSize(_, 1))
      case _ =>
    }

    result.setItemDamage(0)
    FMLCommonHandler.instance.firePlayerCraftingEvent(player, result, new InventoryFake(1))

    val knowledge = ThaumcraftCapabilities.getKnowledge(player)
    val re = ResearchCategories.getResearch("aa_wands")
    if (knowledge.getResearchStage(re.getKey) == 2)
      knowledge.addResearch("[#]" + ResearchManager.createItemStackHash(ItemUtils.byBlock(BlockWandBuilder)))

    result.setItemDamage(player.getHorizontalFacing.ordinal())
    super.execute(world, player, pos, placement, side)
  }
}
