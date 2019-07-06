package hohserg.advancedauromancy.blocks

import hohserg.advancedauromancy.core.Main
import hohserg.advancedauromancy.items.{ItemWandCasting, ItemWandComponent}
import hohserg.advancedauromancy.nbt.Nbt
import hohserg.advancedauromancy.utils.ItemUtils
import hohserg.advancedauromancy.wands._
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.InventoryBasic
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumBlockRenderType, EnumFacing, EnumHand}
import net.minecraft.world.{World, WorldServer}
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.registries.IForgeRegistry
import thaumcraft.common.items.casters.ItemCaster

object BlockWandBuilder extends BlockContainer(Material.ROCK) {

  override def getRenderType(state: IBlockState) = EnumBlockRenderType.MODEL

  override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    if (!worldIn.isRemote) {
      Option(worldIn.getTileEntity(pos))
        .collect({ case tile: TileWandBuilder => tile }).foreach(tile =>
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

  type ComponentByStack[A] = ItemStack => Option[A]

  def getByRegistry[A <: WandComponentRegistryEntry[A]](registry: IForgeRegistry[A]): ComponentByStack[A] =
    itemStack => Option(registry.getValue(ItemWandComponent.getComponentKey(itemStack))).filter(!_.isDefault)

  lazy val capByStack: ComponentByStack[WandCap] = getByRegistry(GameRegistry.findRegistry(classOf[WandCap]))

  lazy val rodByStack: ComponentByStack[WandRod] = getByRegistry(GameRegistry.findRegistry(classOf[WandRod]))

  lazy val upgradeByStack: ComponentByStack[WandUpgrade] = getByRegistry(GameRegistry.findRegistry(classOf[WandUpgrade]))


  val craftMatrix: Vector[Vector[Option[(ComponentByStack[_], Int)]]] = Vector(
    Vector(None, None, None, Some((upgradeByStack, 13)), Some((upgradeByStack, 12))),
    Vector(None, None, Some((upgradeByStack, 4)), Some((capByStack, 1)), Some((upgradeByStack, 11))),
    Vector(None, Some((upgradeByStack, 5)), Some((rodByStack, 0)), Some((upgradeByStack, 6)), None),
    Vector(Some((upgradeByStack, 10)), Some((capByStack, 2)), Some((upgradeByStack, 7)), None, None),
    Vector(Some((upgradeByStack, 8)), Some((upgradeByStack, 9)), None, None, None)
  )

  class TileWandBuilder extends TileEntity {

    def tryInsertItemStack(stack: ItemStack, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
      if (hitY == 1 && resultSlot.isEmpty) {
        val x = math.min((hitX / 0.2).toInt, 4)
        val z = math.min((hitZ / 0.2).toInt, 4)
        craftMatrix(x)(z)
          .filter(_._1(stack).nonEmpty)
          .map(_._2)
          .exists { slotIndex =>
            val takeOne = stack.copy()
            takeOne.setCount(1)
            stack.shrink(1)
            inv.setInventorySlotContents(slotIndex, takeOne)
            sendUpdates()
            markDirty()
            true
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

        craftRodAndCaps(rodSlot, capSlot, upgrades.map(_._1))
          .foreach({
            for {i <- upgrades}
              inv.decrStackSize(i._2, 1)
            for {i <- 0 to 2}
              inv.decrStackSize(i, 1)
            inv.setInventorySlotContents(3, _)
          })
        sendUpdates()
        markDirty()
        //world.markAndNotifyBlock(pos,null,)
      }
    }

    def craftRodAndCaps(rodStack: ItemStack, capStack: ItemStack, upgrades: Seq[WandUpgrade]): Option[ItemStack] = {
      capByStack(capStack).flatMap(cap =>
        rodByStack(rodStack).map(rod =>
          Map("cap" -> cap.name, "rod" -> rod.name, "wandUpgrades" -> upgrades.map(_.name))
        ))
        .map(Nbt.fromMap)
        .map {
          nbt =>
            val r = new ItemStack(ItemWandCasting)
            r.setTagCompound(nbt)
            r
        }
    }

    val inv = new InventoryBasic("Wand Builder", true, 14)

    override def writeToNBT(tagCompound: NBTTagCompound): NBTTagCompound = {
      val list = new NBTTagList
      for {
        i <- 0 until inv.getSizeInventory
        item = inv.getStackInSlot(i)
        if !item.isEmpty
      } {
        val comp = new NBTTagCompound
        comp.setByte("Slot", i.toByte)
        item.writeToNBT(comp)
        list.appendTag(comp)
      }
      tagCompound.setTag("Items", list)

      super.writeToNBT(tagCompound)
    }

    override def readFromNBT(tagCompound: NBTTagCompound): Unit = {
      val list = tagCompound.getTagList("Items", Constants.NBT.TAG_COMPOUND)
      for (i <- 0 until list.tagCount()) {
        val comp = list.getCompoundTagAt(i)
        val j = comp.getByte("Slot") & 255
        comp.removeTag("Slot")
        if (j >= 0 && j < inv.getSizeInventory) inv.setInventorySlotContents(j, new ItemStack(comp))
      }

      super.readFromNBT(tagCompound)
    }

    override def getUpdateTag: NBTTagCompound = writeToNBT(new NBTTagCompound)

    override def onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity): Unit = {
      readFromNBT(pkt.getNbtCompound)
    }

    override def getUpdatePacket: SPacketUpdateTileEntity = {
      val tagCompound = new NBTTagCompound
      writeToNBT(tagCompound)
      new SPacketUpdateTileEntity(pos, 3, tagCompound)
    }

    def sendUpdates(): Unit = {
      val packet = getUpdatePacket
      if (packet != null && world.isInstanceOf[WorldServer]) {
        val chunk = world.asInstanceOf[WorldServer].getPlayerChunkMap.getEntry(pos.getX >> 4, pos.getZ >> 4)
        if (chunk != null) chunk.sendPacket(packet)
      }
    }
  }

}
