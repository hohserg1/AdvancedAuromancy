package hohserg.advancedauromancy.blocks

import net.minecraft.block.BlockContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityItem
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

trait DropOnBreak extends BlockContainer {
  override def breakBlock(worldIn: World, pos: BlockPos, state: IBlockState): Unit = {
    val items =
      Option(worldIn.getTileEntity(pos))
        .collect { case tile: BaseInventoryTile => tile }
        .map(tile => 0 until tile.inv.getSlots map tile.inv.getStackInSlot)
    super.breakBlock(worldIn, pos, state)
    items.foreach(_.foreach(i => worldIn.spawnEntity(new EntityItem(worldIn, pos.getX, pos.getY, pos.getZ, i))))
  }
}
