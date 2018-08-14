package hohserg.advancedauromancy.blocks

import hohserg.advancedauromancy.items.ItemWandCasting
import hohserg.advancedauromancy.nbt.Nbt._
import hohserg.advancedauromancy.wands._
import hohserg.advancedauromancy.{ItemUtils, Main}
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
import thaumcraft.common.items.casters.ItemCaster

object BlockWandBuilder extends BlockContainer(Material.ROCK){
  //override def isOpaqueCube(state: IBlockState) = false

  //override def isFullCube(state: IBlockState) = false

  override def getRenderType(state: IBlockState) = EnumBlockRenderType.MODEL

  override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float):Boolean = {
    if(!worldIn.isRemote) {
      Option(worldIn.getTileEntity(pos))
        .collect({ case tile: TileWandBuilder => tile }).foreach(tile=>
          playerIn.getHeldItem(hand).getItem match {
            case caster: ItemCaster => tile.craft(playerIn, caster)
            case item =>
              if(!tile.tryInsertItemStack(playerIn.getHeldItem(hand),hitX, hitY, hitZ))
                playerIn.openGui(Main, 0, worldIn, pos.getX, pos.getY, pos.getZ)
              tile.markDirty()
          })
    }
    true
  }
  override def createNewTileEntity(worldIn: World, meta: Int): TileEntity = new TileWandBuilder

  val craftMatrix=Vector(
    Vector(None,None,None,Some((WandUpgrade,13)),Some((WandUpgrade,12))),
    Vector(None,None,Some((WandUpgrade,4)),Some((WandCap,1)),Some((WandUpgrade,11))),
    Vector(None,Some((WandUpgrade,5)),Some((WandRod,0)),Some((WandUpgrade,6)),None),
    Vector(Some((WandUpgrade,10)),Some((WandCap,2)),Some((WandUpgrade,7)),None,None),
    Vector(Some((WandUpgrade,8)),Some((WandUpgrade,9)),None,None,None)
  )

  class TileWandBuilder extends TileEntity{

    override def writeToNBT(tagCompound: NBTTagCompound): NBTTagCompound = {
      val list = new NBTTagList
      for{
        i<-0 until inv.getSizeInventory
        item=inv.getStackInSlot(i)
        if !item.isEmpty
      }{
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
      for(i<-0 until list.tagCount()){
        val comp = list.getCompoundTagAt(i)
        val j = comp.getByte("Slot") & 255
        comp.removeTag("Slot")
        if (j >= 0 && j < inv.getSizeInventory) inv.setInventorySlotContents(j,new ItemStack(comp))
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

    def sendUpdates() = {
        val packet = getUpdatePacket
        if (packet != null && world.isInstanceOf[WorldServer]) {
          val chunk = world.asInstanceOf[WorldServer].getPlayerChunkMap.getEntry(pos.getX >> 4, pos.getZ >> 4)
          if (chunk != null) chunk.sendPacket(packet)
        }
    }

    def tryInsertItemStack(stack: ItemStack, hitX: Float, hitY: Float, hitZ:Float):Boolean = {
      if(hitY==1 && resultSlot.isEmpty){
        val x=math.min((hitX/0.2).toInt,4)
        val z=math.min((hitZ/0.2).toInt,4)
        craftMatrix(x)(z).flatMap(i => i._1(ItemUtils.getItemStackKey(stack)).map(_ => i._2)).exists(i => {
          val takeOne=stack.copy()
          takeOne.setCount(1)
          stack.shrink(1)
          inv.setInventorySlotContents(i, takeOne)
          sendUpdates()
          markDirty()
          true
        })
      }else
        false
    }

    def capSlot: ItemStack ={
      val item=inv.getStackInSlot _
      if(ItemUtils.equalsStacks(item(1),item(2))) item(1)
      else ItemStack.EMPTY
    }
    
    def rodSlot: ItemStack = inv.getStackInSlot(0)

    def resultSlot: ItemStack = inv.getStackInSlot(3)

    def craft(playerIn: EntityPlayer,tool:ItemCaster): Unit ={
      val item=inv.getStackInSlot _
      if(resultSlot.isEmpty) {
        val upgrades = {
          for {i <- 4 to 8 if item(i) != null} yield
            WandUpgrade(ItemUtils.getItemStackKey(item(i))).map(w=>(w,i))
        }.flatten

        craftRodAndCaps(rodSlot, capSlot,upgrades.map(_._1))
          .foreach({
            for {i <- upgrades}
              inv.decrStackSize(i._2,1)
            for {i <- 0 to 2}
              inv.decrStackSize(i,1)
            inv.setInventorySlotContents(3,_)
          })
        sendUpdates()
        markDirty()
        //world.markAndNotifyBlock(pos,null,)
      }
    }

    def craftRodAndCaps(rodStack: ItemStack, capStack: ItemStack,upgrades: Seq[WandUpgrade]): Option[ItemStack] = {
      WandCap(ItemUtils.getItemStackKey(capStack))
        .flatMap(cap=> WandRod(ItemUtils.getItemStackKey(rodStack))
            .map(rod=>
              Map("cap"->cap.name,"rod"->rod.name,"wandUpgrades"->upgrades.map(_.name))
            ))
        .map(nbt=>{
          val i=new ItemStack(ItemWandCasting,1)
          i.setTagCompound(nbt)
          i
        })
    }

    val inv = new InventoryBasic("Wand Builder", true, 14)
  }
}
