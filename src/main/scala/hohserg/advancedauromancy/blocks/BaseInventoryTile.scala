package hohserg.advancedauromancy.blocks

import javax.annotation.Nonnull
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.world.WorldServer
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.{CapabilityItemHandler, ItemStackHandler}

class BaseInventoryTile(size: Int) extends TileEntity {

  def inv = itemHandler

  private var itemHandler = newItemStackHandler

  override def writeToNBT(tagCompound: NBTTagCompound): NBTTagCompound = {
    val r = super.writeToNBT(tagCompound)
    r.merge(itemHandler.serializeNBT())
    r
  }

  override def readFromNBT(compound: NBTTagCompound): Unit = {
    super.readFromNBT(compound)
    itemHandler = newItemStackHandler
    itemHandler.deserializeNBT(compound)
  }

  override def getUpdateTag: NBTTagCompound = writeToNBT(new NBTTagCompound)

  override def onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity): Unit =
    readFromNBT(pkt.getNbtCompound)

  override def getUpdatePacket: SPacketUpdateTileEntity =
    new SPacketUpdateTileEntity(pos, 3, getUpdateTag)


  private def newItemStackHandler = new ItemStackHandler(size) {
    override def onContentsChanged(slot: Int): Unit = sendUpdates()
  }

  override def hasCapability(@Nonnull cap: Capability[_], side: EnumFacing): Boolean =
    cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side)

  override def getCapability[T](@Nonnull cap: Capability[T], side: EnumFacing): T =
    if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
      CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemHandler)
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
