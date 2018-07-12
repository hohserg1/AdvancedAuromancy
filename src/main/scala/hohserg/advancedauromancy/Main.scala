package hohserg.advancedauromancy

import net.minecraft.block.{Block, BlockContainer}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.{Item, ItemBlock, ItemStack}
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.fml.common.event.{FMLConstructionEvent, FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.network.{IGuiHandler, NetworkRegistry}
import net.minecraftforge.fml.common.registry.{ForgeRegistries, GameRegistry}
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import thaumcraft.api.ThaumcraftApi
import thaumcraft.api.aspects.{Aspect, AspectList}
import thaumcraft.api.research.ResearchCategories

@Mod(name="AdvancedAuromancy",modid = Main.advancedAuromancyModId, version="1.0",modLanguage = "scala",dependencies = "required-after:thaumcraft")
object Main {
  @SidedProxy(clientSide = "hohserg.advancedauromancy.ClientProxy",serverSide = "hohserg.advancedauromancy.ServerProxy")
  var proxy:CommonProxy=null

  final val advancedAuromancyModId="advancedauromancy"

  @Mod.EventHandler def construct(event: FMLConstructionEvent): Unit = {
    //SubscribeModule.extractAsSequence(event.getASMHarvestedData)
  }

  @Mod.EventHandler def preinit(event: FMLPreInitializationEvent): Unit = {
    proxy.preinit(event)
    NetworkRegistry.INSTANCE.registerGuiHandler(Main, proxy)
  }

  @Mod.EventHandler def init(event: FMLInitializationEvent): Unit = {
    proxy.init(event)
  }

  @Mod.EventHandler def postinit(event: FMLPostInitializationEvent): Unit = {
    proxy.postinit(event)
  }
}
class ServerProxy extends CommonProxy{

}

class ClientProxy extends CommonProxy{
  override def init(event: FMLInitializationEvent): Unit = {
    super.init(event)
  }
  override def preinit(event: FMLPreInitializationEvent): Unit = {
    super.preinit(event)
  }
}

class CommonProxy extends IGuiHandler{
  private lazy val tab=new CreativeTabs(Main.advancedAuromancyModId) {
    override def getTabIconItem = new ItemStack(Items.APPLE)
  }

  import Main._

  private val blocks=Array[Block]()
  private val items=Array[Item]()

  def preinit(event: FMLPreInitializationEvent): Unit = {
    event.getModMetadata.authorList add "hohserg"

    items.foreach(item=>{
      val name=item.getClass.getSimpleName.dropRight(1).toLowerCase
      item.setUnlocalizedName(name).setCreativeTab(tab)
      ForgeRegistries.ITEMS.register(if(item.getRegistryName==null)item.setRegistryName(name) else item )
    })

    blocks.foreach(block=>{
      val name=block.getClass.getSimpleName.dropRight(1).toLowerCase
      ForgeRegistries.BLOCKS.register(block.setUnlocalizedName(name).setRegistryName(name).setCreativeTab(tab))
      ForgeRegistries.ITEMS.register(new ItemBlock(block).setRegistryName(name))
      block match {
        case container: BlockContainer =>
          val tile = container.createNewTileEntity(null, 0).getClass
          GameRegistry.registerTileEntity(tile, tile.getSimpleName)
        case _ =>
      }
    })

    Savable.preinit(event)
  }

  def init(event: FMLInitializationEvent): Unit = {
  }

  def postinit(event: FMLPostInitializationEvent): Unit = {
    ResearchCategories.registerCategory(advancedAuromancyModId.toUpperCase,"FLUX",
      new AspectList().add(Aspect.AURA,1).add(Aspect.CRAFT,1).add(Aspect.MAGIC,1),
      new ResourceLocation(advancedAuromancyModId,"textures/icon.png"),
      new ResourceLocation(advancedAuromancyModId,"textures/background.png")
    )
    ThaumcraftApi.registerResearchLocation(new ResourceLocation(Main.advancedAuromancyModId, "research/research.json"))

  }

  override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = ID match{
    case 0=>null

  }

  override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = ID match{
    case 0=>null

  }
}