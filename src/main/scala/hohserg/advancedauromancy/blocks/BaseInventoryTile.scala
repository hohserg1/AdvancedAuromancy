package hohserg.advancedauromancy.blocks

import javax.annotation.Nonnull
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.world.WorldServer
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.{CapabilityItemHandler, ItemStackHandler}

abstract class BaseInventoryTile(size: Int) extends TileEntity {
  type ISH <: ItemStackHandler

  def invFactory(size: Int, baseTile: BaseInventoryTile): ISH

  val inv: ISH = invFactory(size, this)

  override def writeToNBT(tagCompound: NBTTagCompound): NBTTagCompound = {
    val r = super.writeToNBT(tagCompound)
    r.merge(inv.serializeNBT())
    r
  }

  override def readFromNBT(compound: NBTTagCompound): Unit = {
    super.readFromNBT(compound)
    inv.deserializeNBT(compound)
  }

  override def getUpdateTag: NBTTagCompound = writeToNBT(new NBTTagCompound)

  override def onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity): Unit =
    readFromNBT(pkt.getNbtCompound)

  override def getUpdatePacket: SPacketUpdateTileEntity =
    new SPacketUpdateTileEntity(pos, 3, getUpdateTag)


  override def hasCapability(@Nonnull cap: Capability[_], side: EnumFacing): Boolean =
    cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side)

  override def getCapability[T](@Nonnull cap: Capability[T], side: EnumFacing): T =
    if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
      CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inv)
    else
      super.getCapability(cap, side)


  def sendUpdates(): Unit =
    world match {
      case server: WorldServer =>
        val chunk = server.getPlayerChunkMap.getEntry(pos.getX >> 4, pos.getZ >> 4)
        if (chunk != null) chunk.sendPacket(getUpdatePacket)
      case _ =>
    }
}

object BaseInventoryTile {

  trait SyncItemStackHandler extends ItemStackHandler {
    def baseInventoryTile: BaseInventoryTile

    override def onContentsChanged(slot: Int): Unit = baseInventoryTile.sendUpdates()

    override def insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack = {
      if (isItemValid(slot, stack))
        super.insertItem(slot, stack, simulate)
      else
        stack
    }
  }

  trait LockableItemStackHandler extends ItemStackHandler {
    var lock = false

    override def insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack =
      if (lock)
        stack
      else
        super.insertItem(slot, stack, simulate)

    override def extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack =
      if (lock)
        ItemStack.EMPTY
      else
        super.extractItem(slot, amount, simulate)

    override def setStackInSlot(slot: Int, stack: ItemStack): Unit =
      if (!lock)
        super.setStackInSlot(slot, stack)

  }

}
