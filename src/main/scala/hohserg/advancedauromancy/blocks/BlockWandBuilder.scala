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
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumBlockRenderType, EnumFacing, EnumHand}
import net.minecraft.world.World
import thaumcraft.common.items.casters.ItemCaster

object BlockWandBuilder extends BlockContainer(Material.ROCK){

  override def getRenderType(state: IBlockState) = EnumBlockRenderType.MODEL

  override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float):Boolean = {
    println(hitX,hitY,hitZ)
    if(!worldIn.isRemote) {
      Option(worldIn.getTileEntity(pos))
        .collect({ case tile: TileWandBuilder => tile }).foreach(tile=>
          playerIn.getHeldItem(hand).getItem match {
            case caster: ItemCaster => tile.craft(playerIn, caster)
            case item =>
              if(!tile.tryInsertItemStack(playerIn.getHeldItem(hand),hitX, hitY, hitZ))
                playerIn.openGui(Main, 0, worldIn, pos.getX, pos.getY, pos.getZ)
          })
    }
    true
  }
  override def createNewTileEntity(worldIn: World, meta: Int): TileEntity = new TileWandBuilder

  val craftMatrix=Vector(
    Vector(None,None,None,Some((WandUpgrade,11)),Some((WandUpgrade,12))),
    Vector(None,None,Some((WandUpgrade,6)),Some((WandCap,1)),Some((WandUpgrade,13))),
    Vector(None,Some((WandUpgrade,7)),Some((WandRod,0)),Some((WandUpgrade,4)),None),
    Vector(Some((WandUpgrade,9)),Some((WandCap,2)),Some((WandUpgrade,5)),None,None),
    Vector(Some((WandUpgrade,8)),Some((WandUpgrade,10)),None,None,None)
  )

  class TileWandBuilder extends TileEntity{
    def tryInsertItemStack(stack: ItemStack, hitX: Float, hitY: Float, hitZ:Float):Boolean = {
      if(hitY==1 && resultSlot.isEmpty){
        val x=math.min((hitX/0.2).toInt,4)
        val z=math.min((hitZ/0.2).toInt,4)
        craftMatrix(x)(z).flatMap(i => i._1(ItemUtils.getItemStackKey(stack)).map(_ => i._2)).exists(i => {
          val takeOne=stack.copy()
          takeOne.setCount(1)
          stack.shrink(1)
          inv.setInventorySlotContents(i, takeOne)
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
              for {i <- 1 to 3}
                inv.decrStackSize(i,1)
              inv.setInventorySlotContents(3,_)
            })
      }
    }

    def craftRodAndCaps(rodStack: ItemStack, capStack: ItemStack,upgrades: Seq[WandUpgrade]): Option[ItemStack] = {
      WandCap(ItemUtils.getItemStackKey(capStack))
        .flatMap(cap=> WandRod(ItemUtils.getItemStackKey(rodStack))
            .map(rod=>
          Map("cap"->cap.name,"rod"->rod.name,"wandUpgrades"->upgrades.map(_.name))
        )).map(new ItemStack(ItemWandCasting,1,0,_))
    }

    val inv = new InventoryBasic("Wand Builder", true, 14)
  }
}
