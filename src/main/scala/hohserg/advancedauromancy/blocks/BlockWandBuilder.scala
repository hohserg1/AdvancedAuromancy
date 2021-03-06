package hohserg.advancedauromancy.blocks

import hohserg.advancedauromancy.blocks.BaseInventoryTile.{LockableItemStackHandler, SyncItemStackHandler}
import hohserg.advancedauromancy.core.Main
import hohserg.advancedauromancy.items.base.Wand._
import hohserg.advancedauromancy.items.{ItemWandCasting, PrimalCharm}
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
import thaumcraft.common.items.casters.ItemCaster
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
              case caster: ItemCaster => tile.craft(playerIn, caster)
              case item =>
                if (!tile.tryInsertItemStack(playerIn.getHeldItem(hand), hitX, hitY, hitZ))
                  playerIn.openGui(Main, 0, worldIn, pos.getX, pos.getY, pos.getZ)
                tile.markDirty()
            })
    }
    true
  }

  override def createNewTileEntity(worldIn: World, meta: Int): TileEntity = new TileWandBuilder


  lazy val craftMatrix: Vector[Vector[Option[(ComponentByStack[_], Int)]]] = Vector(
    Vector(None, None, None, Some((upgradeByStack, 13)), Some((upgradeByStack, 12))),
    Vector(None, None, Some((upgradeByStack, 4)), Some((capByStack, 1)), Some((upgradeByStack, 11))),
    Vector(None, Some((upgradeByStack, 5)), Some((rodByStack, 0)), Some((upgradeByStack, 6)), None),
    Vector(Some((upgradeByStack, 10)), Some((capByStack, 2)), Some((upgradeByStack, 7)), None, None),
    Vector(Some((upgradeByStack, 8)), Some((upgradeByStack, 9)), None, None, None)
  )

  lazy val checkSlot = craftMatrix.flatten.flatten.map { case (check, slot) => slot -> check }.toMap

  class TileWandBuilder extends BaseInventoryTile(14) {

    override type ISH = ItemStackHandler with SyncItemStackHandler with LockableItemStackHandler

    override def invFactory(size: Int, baseTile: BaseInventoryTile): ISH = new ItemStackHandler(14) with SyncItemStackHandler with LockableItemStackHandler {
      override def baseInventoryTile: BaseInventoryTile = TileWandBuilder.this

      override def isItemValid(slot: Int, stack: ItemStack): Boolean =
        checkSlot.get(slot).exists(_ (stack).isDefined)
    }

    def tryInsertItemStack(stack: ItemStack, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
      if (hitY == 1 && resultSlot.isEmpty) {

        val facingAngle = -math.toRadians(world.getBlockState(pos).getValue(FACING).getHorizontalAngle)

        val rHitX = 1-(0.5 + (hitX - 0.5) * math.cos(facingAngle) - (hitZ - 0.5) * math.sin(facingAngle))
        val rHitZ = 1-(0.5 + (hitZ - 0.5) * math.cos(facingAngle) + (hitX - 0.5) * math.sin(facingAngle))

        val x = math.min((rHitX / 0.2).toInt, 4)
        val z = math.min((rHitZ / 0.2).toInt, 4)
        craftMatrix(x)(z)
          .filter(_._1(stack).nonEmpty)
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

    def craft(playerIn: EntityPlayer, tool: ItemCaster): Unit = {
      val item = inv.getStackInSlot _
      if (resultSlot.isEmpty) {
        val upgrades = {
          for {
            i <- 4 to 8
            stack = item(i)
            if !stack.isEmpty
          } yield
            upgradeByStack(stack).map(_ -> i)
        }.flatten

        craftRodAndCaps(rodSlot, capSlot, upgrades.map(_._1), playerIn)
          .foreach { wand =>
            inv.lock = true
            world.addBlockEvent(pos, getBlockType, 5, 1)


            ServerEvents.addRunnableServer(world, new Runnable {
              override def run(): Unit = {
                inv.lock = false

                println("test")
                for {i <- upgrades}
                  inv.extractItem(i._2, 1, simulate = false)

                for {i <- 0 to 2}
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

    def craftRodAndCaps(rodStack: ItemStack, capStack: ItemStack, upgrades: Seq[WandUpgrade], playerIn: EntityPlayer): Option[ItemStack] = {
      capByStack(capStack).flatMap { cap =>
        rodByStack(rodStack).map { rod =>
          val data = Map(wandCapKey -> cap.name, wandRodKey -> rod.name, wandUpgradesKey -> upgrades.map(_.name))
          if (cap == EnderCap)
            (data + (enderKeyTag -> playerIn.getName)) -> ItemEnderWandCasting
          else
            data -> ItemWandCasting
        }
      }.map { case (data, wand) =>
        val r = new ItemStack(wand)
        r.setTagCompound(Nbt.fromMap(data))
        r
      }
    }
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
