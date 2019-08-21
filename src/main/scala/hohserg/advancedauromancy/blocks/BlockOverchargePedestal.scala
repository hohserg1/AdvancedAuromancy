package hohserg.advancedauromancy.blocks

import hohserg.advancedauromancy.blocks.BaseInventoryTile.SyncItemStackHandler
import hohserg.advancedauromancy.endervisnet.EnderVisNet
import hohserg.advancedauromancy.items.ItemEnderWandCasting
import hohserg.advancedauromancy.items.base.Wand
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemStack
import net.minecraft.util._
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import net.minecraftforge.items.ItemStackHandler
import thaumcraft.api.aspects.{Aspect, AspectList, IAspectContainer}
import thaumcraft.api.aura.AuraHelper
import thaumcraft.api.items.{IRechargable, RechargeHelper}
import thaumcraft.client.fx.FXDispatcher

object BlockOverchargePedestal extends BlockContainer(Material.ROCK) with DropOnBreak {
  override def getRenderType(state: IBlockState) = EnumBlockRenderType.MODEL

  override def isOpaqueCube(state: IBlockState) = false

  override def isFullCube(state: IBlockState) = false

  override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean =
    if (worldIn.isRemote)
      true
    else {
      Option(worldIn.getTileEntity(pos))
        .collect { case tile: TileOverchargePedestal => tile }
        .exists { tile =>
          val itemStack = tile.inv.getStackInSlot(0)
          if (itemStack.isEmpty && playerIn.inventory.getCurrentItem.getItem.isInstanceOf[IRechargable]) {
            val i = playerIn.getHeldItem(hand).copy
            i.setCount(1)
            tile.inv.setStackInSlot(0, i)
            playerIn.getHeldItem(hand).shrink(1)

            if (playerIn.getHeldItem(hand).getCount == 0) playerIn.setHeldItem(hand, ItemStack.EMPTY)
            playerIn.inventory.markDirty()
            worldIn.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2F, ((worldIn.rand.nextFloat - worldIn.rand.nextFloat) * 0.7F + 1.0F) * 1.6F)
            true
          } else if (!itemStack.isEmpty) {
            tile.inv.setStackInSlot(0, ItemStack.EMPTY)
            worldIn.spawnEntity(new EntityItem(worldIn, playerIn.posX, playerIn.posY + 1, playerIn.posZ, itemStack))
            worldIn.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2F, ((worldIn.rand.nextFloat - worldIn.rand.nextFloat) * 0.7F + 1.0F) * 1.5F)
            true
          } else
            false
        }
    }

  override def createNewTileEntity(worldIn: World, meta: Int) = new TileOverchargePedestal

  class TileOverchargePedestal extends BaseInventoryTile(1) with ITickable with IAspectContainer {

    override type ISH = ItemStackHandler with SyncItemStackHandler

    override def invFactory(size: Int, baseTile: BaseInventoryTile): ISH =
      new ItemStackHandler(1) with SyncItemStackHandler {
        override def isItemValid(slot: Int, stack: ItemStack): Boolean = stack.getItem.isInstanceOf[IRechargable] || stack.isEmpty

        override def baseInventoryTile: BaseInventoryTile = TileOverchargePedestal.this
      }

    var tick = 0

    override def update(): Unit = {
      val itemStack = inv.getStackInSlot(0)
      if (!world.isRemote && tick == 0) {
        itemStack.getItem match {
          case ItemEnderWandCasting =>
            EnderVisNet
              .getVisNetByStack(itemStack)
              .foreach { net =>
                val amt = Math.min(5, net.getMaxVis - net.getVis)
                val drained = AuraHelper.drainVis(world, pos, amt, false)
                net.addVis(drained, ItemEnderWandCasting.getMaxVis(itemStack))
              }
          case _ =>
            if (!itemStack.isEmpty && RechargeHelper.rechargeItem(world, itemStack, pos, null, 5) > 0.0F) {
              sendUpdates()
              markDirty()
              val al = Aspect.getPrimalAspects
              world.addBlockEvent(pos, getBlockType, 5, al.get(world.rand.nextInt(al.size)).getColor)
            }
        }
      }

      tick += 1
      if (tick == 10)
        tick = 0
    }

    @SideOnly(Side.CLIENT)
    override def getRenderBoundingBox: AxisAlignedBB =
      new AxisAlignedBB(pos).grow(2.0D, 2.0D, 2.0D)

    override def getAspects: AspectList =
      Option(inv.getStackInSlot(0))
        .filter(!_.isEmpty)
        .map(stack =>
          Wand.wand(stack)(wand =>
            wand.getVis(stack).toInt, RechargeHelper.getCharge(stack)))
        .map(new AspectList().add(Aspect.AURA, _))
        .orNull

    override def setAspects(aspectList: AspectList): Unit = ()

    override def doesContainerAccept(aspect: Aspect): Boolean = true

    override def addToContainer(aspect: Aspect, i: Int): Int = 0

    override def takeFromContainer(aspect: Aspect, i: Int): Boolean = false

    override def takeFromContainer(aspectList: AspectList): Boolean = false

    override def doesContainerContainAmount(aspect: Aspect, i: Int): Boolean = false

    override def doesContainerContain(aspectList: AspectList): Boolean = false

    override def containerContains(aspect: Aspect): Int = 0

    override def receiveClientEvent(i: Int, j: Int): Boolean =
      if (i == 5) {
        if (world.isRemote)
          FXDispatcher.INSTANCE.visSparkle(
            pos.getX + world.rand.nextInt(3) - world.rand.nextInt(3),
            pos.up.getY + world.rand.nextInt(3),
            pos.getZ + world.rand.nextInt(3) - world.rand.nextInt(3),
            pos.getX, pos.up.getY, pos.getZ, j)
        true
      } else false
  }

}
