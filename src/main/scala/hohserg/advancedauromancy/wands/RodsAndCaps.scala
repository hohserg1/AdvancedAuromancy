package hohserg.advancedauromancy.wands

import java.util

import hohserg.advancedauromancy.Main.advancedAuromancyModId
import hohserg.advancedauromancy.client.TextureRegister
import hohserg.advancedauromancy.client.render.IBakedSource
import hohserg.advancedauromancy.items.{ItemCap, ItemRod, ItemUpgrade}
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraftforge.fml.common.registry.ForgeRegistries

import scala.collection.mutable

object RodsAndCaps{
  val identityOnUpdate: (ItemStack, EntityPlayer) => Unit = WandRod.identityOnUpdate
  lazy val testUpgrade=WandUpgrade.register(0,1,"TestUpgrade",100,identityOnUpdate,new IBakedSource {
    override def getQuads(state: IBlockState, side: EnumFacing, rand: Long): util.List[BakedQuad] = util.Collections.emptyList()
  })
  def initRodAndCaps() = {
    WandCap.register(0.7f,"GoldCap",100,new ResourceLocation(advancedAuromancyModId+":rods_and_caps/wand_cap_gold"))
    WandCap.register(0.7f,"ThaumiumCap",100,new ResourceLocation(advancedAuromancyModId+":rods_and_caps/wand_cap_thaumium"))
    WandCap.register(0.7f,"VoidCap",100,new ResourceLocation(advancedAuromancyModId+":rods_and_caps/wand_cap_void"))
    WandCap.register(0.7f,"AuramCap",100,new ResourceLocation(advancedAuromancyModId+":rods_and_caps/wand_cap_aurum"))
    WandCap.register(0.7f,"EnderCap",100,new ResourceLocation(advancedAuromancyModId+":rods_and_caps/wand_cap_ender"))

    testUpgrade

    WandRod.register(100, "GreatwoodRod",0,identityOnUpdate,new ResourceLocation(advancedAuromancyModId+":rods_and_caps/wand_rod_greatwood"))
    WandRod.register(100, "SilverwoodRod",0,identityOnUpdate,new ResourceLocation(advancedAuromancyModId+":rods_and_caps/wand_rod_silverwood"))
    WandRod.register(100, "TaintwoodRod",0,identityOnUpdate,new ResourceLocation(advancedAuromancyModId+":rods_and_caps/wand_rod_taintrwood"))

    WandRod.register(100, "BirchRod",0,identityOnUpdate,new ResourceLocation(advancedAuromancyModId+":rods_and_caps/wand_rod_birchwood"))
    WandRod.register(100, "OakRod",0,identityOnUpdate,new ResourceLocation(advancedAuromancyModId+":rods_and_caps/wand_rod_oakwood"))
    WandRod.register(100, "SpruceRod",0,identityOnUpdate,new ResourceLocation(advancedAuromancyModId+":rods_and_caps/wand_rod_sprucewood"))
    WandRod.register(100, "JungleRod",0,identityOnUpdate,new ResourceLocation(advancedAuromancyModId+":rods_and_caps/wand_rod_junglewood"))

    DefaultCap
    DefaultRod

    WandUpgrade.register(0,1,"ChargeIndicator",10,identityOnUpdate,IBakedSource.empty)

  }

}

trait IndexedElem{
  def item:Item
  ForgeRegistries.ITEMS.register(item)
  override def hashCode(): Int = name.hashCode
  def name: String = item.delegate.name().toString

  override def toString: String = getClass.getSimpleName+" "+name

}

trait Indexed[A<:IndexedElem]{
  private val index=new mutable.OpenHashMap[String,A]()
  def apply(name:String):Option[A]=index.get(name)
  def update(name: String, value: A): Unit = index(name) = value
}

object WandCap extends Indexed[WandCap]{
  def register(discount:Float, item:Item, craftCost:Int, resourceLocation: ResourceLocation):WandCap =
    new WandCap(discount, item, craftCost, resourceLocation)

  def register(discount:Float, name:String, craftCost:Int, resourceLocation: ResourceLocation):WandCap = {
    val item=new ItemCap(name.toLowerCase)
    new WandCap(discount, item, craftCost, resourceLocation)
  }
}
object WandRod extends Indexed[WandRod]{
  val identityOnUpdate: (ItemStack, EntityPlayer) => Unit = (_, _) => ()

  def register(capacity:Int, item:Item, craftCost:Int, onUpdate:(ItemStack,EntityPlayer)=>Unit, resourceLocation: ResourceLocation):WandRod =
    new WandRod(capacity, item, craftCost, onUpdate, resourceLocation)

  def register(capacity:Int, name:String, craftCost:Int, onUpdate:(ItemStack,EntityPlayer)=>Unit, resourceLocation: ResourceLocation):WandRod = {
    val item=new ItemCap(name.toLowerCase)
    new WandRod(capacity, item, craftCost, onUpdate, resourceLocation)
  }
}
object WandUpgrade extends Indexed[WandUpgrade]{
  def register(capacity:Int, discount:Float, name:String, craftCost:Int, onUpdate:(ItemStack,EntityPlayer)=>Unit, model: IBakedSource):WandUpgrade = {
    val item=new ItemUpgrade(name.toLowerCase)
    new WandUpgrade(capacity, discount, item, craftCost, onUpdate, model)
  }

  def register(capacity:Int, discount:Float, item:Item, craftCost:Int, onUpdate:(ItemStack,EntityPlayer)=>Unit,resourceLocation: IBakedSource):WandUpgrade =
    new WandUpgrade(capacity, discount, item, craftCost, onUpdate,resourceLocation)
}

class WandCap(val discount:Float,val item:Item,val craftCost:Int,val location: ResourceLocation) extends TextureRegister with IndexedElem{
  WandCap(name)=this
}
class WandRod(val capacity:Int,val item:Item,val craftCost:Int,val onUpdate:(ItemStack,EntityPlayer)=>Unit,val location: ResourceLocation)  extends TextureRegister with IndexedElem{
  WandRod(name)=this
}
class WandUpgrade(val capacity:Int, val discount:Float, val item:Item, val craftCost:Int, val onUpdate:(ItemStack,EntityPlayer)=>Unit, val model: IBakedSource) extends IndexedElem {
  WandUpgrade(name)=this
}

object DefaultRod extends WandRod(100, new ItemRod("defaultrod"),0,WandRod.identityOnUpdate,new ResourceLocation(advancedAuromancyModId+":rods_and_caps/wand_rod_silverwood"))
object DefaultCap extends WandCap(0.7f, new ItemCap("defaultcap"),100,new ResourceLocation(advancedAuromancyModId+":rods_and_caps/wand_cap_thaumium"))


