package hohserg.advancedauromancy.blocks

import hohserg.advancedauromancy.blocks.BaseInventoryTile.{LockableItemStackHandler, SyncItemStackHandler}
import hohserg.advancedauromancy.core.Main
import hohserg.advancedauromancy.items.base.Wand._
import hohserg.advancedauromancy.items.ItemWandCasting
import hohserg.advancedauromancy.items.charms.ImprovedCharm
import hohserg.advancedauromancy.nbt.Nbt
import hohserg.advancedauromancy.utils.ItemUtils
import hohserg.advancedauromancy.wands.RodsAndCaps._
import net.minecraft.block.BlockContainer
import net.minecraft.block.BlockHorizontal.FACING
import net.minecraft.block.material.Material
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumBlockRenderType, EnumFacing, EnumHand}
import net.minecraft.world.World
import net.minecraftforge.items.ItemStackHandler
import thaumcraft.api.aspects.Aspect
import thaumcraft.client.fx.FXDispatcher
import thaumcraft.common.items.casters.{ItemCaster, ItemFocus}
import thaumcraft.common.lib.events.ServerEvents

object BlockWandBuilder extends BlockContainer(Material.ROCK) with DropOnBreak {

  override def getRenderType(state: IBlockState) = EnumBlockRenderType.MODEL

  override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    if (!worldIn.isRemote) {
      Option(worldIn.getTileEntity(pos))
        .collect { case tile: TileWandBuilder => tile }
        .foreach(tile =>
          if (!tile.inv.lock)
            playerIn.getHeldItem(hand).getItem match {
              case caster: ItemCaster => tile.craftWand(playerIn, caster, playerIn.getHeldItem(hand))
              case item =>
                if (!tile.tryInsertItemStack(playerIn.getHeldItem(hand), hitX, hitY, hitZ))
                  playerIn.openGui(Main, 0, worldIn, pos.getX, pos.getY, pos.getZ)
                tile.markDirty()
            })
    }
    true
  }

  override def createNewTileEntity(worldIn: World, meta: Int): TileEntity = new TileWandBuilder


  lazy val craftMatrix: Vector[Vector[Option[(ItemStack => Boolean, Int)]]] = {
    def rodUpg(v: Int) = Some((rodUpgradeByStack.andThen(_.isDefined), v))

    def capUpg(v: Int) = Some((capUpgradeByStack.andThen(_.isDefined), v))

    def cap(v: Int) = Some((capByStack.andThen(_.isDefined), v))

    def capOrCharm(v: Int) = Some(((is: ItemStack) => capByStack(is).isDefined || is.getItem == ImprovedCharm, v))

    def rodUpgOrCap(v: Int) = Some(((is: ItemStack) => rodUpgradeByStack(is).isDefined || capByStack(is).isDefined, v))

    def capUpgOrFocus(v: Int) = Some(((is: ItemStack) => capUpgradeByStack(is).isDefined || is.getItem.isInstanceOf[ItemFocus], v))

    def rod(v: Int) = Some((rodByStack.andThen(_.isDefined), v))

    val ___ = None

    //@formatter:off
    Vector(
      Vector(___,         ___,        ___,              capUpg(13),       capUpgOrFocus(12)),
      Vector(___,         ___,        rodUpgOrCap(4),   capOrCharm(1),    capUpg(11)),
      Vector(___,         rodUpg(5),  rod(0),           rodUpgOrCap(6),   ___),
      Vector(capUpg(10), cap(2),      rodUpg(7),        ___,              ___),
      Vector(capUpg(8),  capUpg(9),   ___,              ___,              ___)
    )
    //@formatter:on
  }


  lazy val checkSlot = craftMatrix.flatten.flatten.map { case (check, slot) => slot -> check }.toMap

  trait WandRecipe {
    def getResult(inv: Int => ItemStack): Option[CraftingResult]
  }

  object RegularWandRecipe extends WandRecipe {
    override def getResult(inv: Int => ItemStack): Option[CraftingResult] =
      for {
        rod <- rodByStack(inv(0))
        cap1 <- capByStack(inv(1))
        cap2 <- capByStack(inv(2))
        if cap1 == cap2

        rodUpgrades =
        for {
          i <- 4 to 7
          stack = inv(i)
          if !stack.isEmpty
        } yield rodUpgradeByStack(stack)
        if rodUpgrades.forall(_.isDefined)

        capUpgrades =
        for {
          i <- 8 to 13
          stack = inv(i)
          if !stack.isEmpty
        } yield capUpgradeByStack(stack)
        if capUpgrades.forall(_.isDefined)


      } yield {
        val fullUpgrades = (rodUpgrades ++ capUpgrades).flatten
        val nbt = Map(wandCapKey -> cap1.name, wandRodKey -> rod.name, wandCapUpgradesKey -> capUpgrades.flatten.map(_.name), wandRodUpgradesKey -> rodUpgrades.flatten.map(_.name))
        val r = new ItemStack(ItemWandCasting)
        r.setTagCompound(Nbt.fromMap(nbt))
        CraftingResult(r, rod.craftCost + cap1.craftCost + fullUpgrades.map(_.craftCost).sum)
      }
  }

  object ScepterRecipe extends WandRecipe {
    override def getResult(inv: Int => ItemStack): Option[CraftingResult] =

      for {
        rod <- rodByStack(inv(0))
        if inv(1).getItem == ImprovedCharm
        cap1 <- capByStack(inv(2))
        cap2 <- capByStack(inv(4))
        cap3 <- capByStack(inv(6))
        if cap1 == cap2 && cap1 == cap3

        rodUpgrades =
        for {
          i <- Seq(5, 7)
          stack = inv(i)
          if !stack.isEmpty
        } yield rodUpgradeByStack(stack)
        if rodUpgrades.forall(_.isDefined)

        capUpgrades =
        for {
          i <- 8 to 13
          if i != 12
          stack = inv(i)
          if !stack.isEmpty
        } yield capUpgradeByStack(stack)
        if capUpgrades.forall(_.isDefined)

        capUpgradeOrFocusStack = inv(12)
        isFocus = capUpgradeOrFocusStack.getItem.isInstanceOf[ItemFocus]
        mayBeUpgrade = capUpgradeByStack(capUpgradeOrFocusStack)
        if capUpgradeOrFocusStack.isEmpty || isFocus || mayBeUpgrade.isDefined

      } yield {
        val fullUpgrades = (rodUpgrades ++ capUpgrades).flatten ++ mayBeUpgrade
        val nbt = Map(isScepterKey -> true, wandCapKey -> cap1.name, wandRodKey -> rod.name, wandCapUpgradesKey -> (capUpgrades.flatten.map(_.name) ++ mayBeUpgrade), wandRodUpgradesKey -> rodUpgrades.flatten.map(_.name))
        val r = new ItemStack(ItemWandCasting)
        r.setTagCompound(Nbt.fromMap(nbt))
        if (isFocus)
          ItemWandCasting.setFocus(r, capUpgradeOrFocusStack)
        CraftingResult(r, rod.craftCost + cap1.craftCost + fullUpgrades.map(_.craftCost).sum)
      }
  }

  case class CraftingResult(wand: ItemStack, visRequired: Float)

  class TileWandBuilder extends BaseInventoryTile(14) {

    override type ISH = ItemStackHandler with SyncItemStackHandler with LockableItemStackHandler

    override def invFactory(size: Int, baseTile: BaseInventoryTile): ISH = new ItemStackHandler(14) with SyncItemStackHandler with LockableItemStackHandler {
      override def baseInventoryTile: BaseInventoryTile = TileWandBuilder.this

      override def isItemValid(slot: Int, stack: ItemStack): Boolean =
        checkSlot.get(slot).exists(_ (stack))
    }

    def tryInsertItemStack(stack: ItemStack, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
      if (hitY == 1 && resultSlot.isEmpty) {

        val facingAngle = -math.toRadians(world.getBlockState(pos).getValue(FACING).getHorizontalAngle)

        val rHitX = 1-(0.5 + (hitX - 0.5) * math.cos(facingAngle) - (hitZ - 0.5) * math.sin(facingAngle))
        val rHitZ = 1-(0.5 + (hitZ - 0.5) * math.cos(facingAngle) + (hitX - 0.5) * math.sin(facingAngle))

        val x = math.min((rHitX / 0.2).toInt, 4)
        val z = math.min((rHitZ / 0.2).toInt, 4)
        craftMatrix(x)(z)
          .filter(_._1(stack))
          .map(_._2)
          .exists { slotIndex =>
            if (inv.getStackInSlot(slotIndex).isEmpty) {
              val takeOne = stack.copy()
              takeOne.setCount(1)

              if (inv.insertItem(slotIndex, takeOne, simulate = false).isEmpty)
                stack.shrink(1)

              sendUpdates()
              markDirty()
              true
            } else
              false
          }
      } else
        false
    }

    def capSlot: ItemStack = {
      val item = inv.getStackInSlot _
      if (ItemUtils.equalsStacks(item(1), item(2))) item(1)
      else ItemStack.EMPTY
    }

    def rodSlot: ItemStack = inv.getStackInSlot(0)

    def resultSlot: ItemStack = inv.getStackInSlot(3)


    def craftWand(playerIn: EntityPlayer, tool: ItemCaster, toolStack: ItemStack): Unit = {
      val item = inv.getStackInSlot _
      if (resultSlot.isEmpty) {
        RegularWandRecipe.getResult(item).orElse(ScepterRecipe.getResult(item))
          .foreach { case CraftingResult(wand, visRequired) =>
            inv.lock = true
            world.addBlockEvent(pos, getBlockType, 5, 1)
            tool.consumeVis(toolStack, playerIn, visRequired, true, false)


            ServerEvents.addRunnableServer(world, new Runnable {
              override def run(): Unit = {
                inv.lock = false

                for {i <- (0 to 2) ++ (4 to 13)}
                  inv.extractItem(i, 1, simulate = false)

                inv.setStackInSlot(3, wand)
              }
            }, 20 * 2)
          }
        sendUpdates()
        markDirty()
      }
    }

    override def receiveClientEvent(i: Int, j: Int): Boolean =
      if (i == 5) {
        if (world.isRemote) {

          val al = Aspect.getPrimalAspects
          val mainColor = Aspect.MAGIC.getColor
          for (i <- 0 to 100) {
            val color = al.get(world.rand.nextInt(al.size)).getColor
            FXDispatcher.INSTANCE.visSparkle(
              pos.getX + world.rand.nextInt(3) - world.rand.nextInt(3),
              pos.up.getY + world.rand.nextInt(3),
              pos.getZ + world.rand.nextInt(3) - world.rand.nextInt(3),
              pos.getX, pos.getY, pos.getZ,
              if (i % 2 == 0) mainColor else color)
          }
        }
        true
      } else false
  }

  setDefaultState(blockState.getBaseState.withProperty(FACING, EnumFacing.NORTH))

  override def createBlockState(): BlockStateContainer = new BlockStateContainer(this, FACING)

  override def getMetaFromState(state: IBlockState): Int = state.getValue(FACING).ordinal()

  override def getStateFromMeta(meta: Int): IBlockState = getDefaultState.withProperty(FACING, EnumFacing.values()(meta))

  override def getStateForPlacement(worldIn: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase): IBlockState =
    getDefaultState.withProperty(FACING, placer.getHorizontalFacing)

  override def onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack): Unit =
    worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing), 2)

}
