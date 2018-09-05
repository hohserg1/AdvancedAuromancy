package hohserg.advancedauromancy

import codechicken.lib.packet.PacketCustom
import hohserg.advancedauromancy.Main.advancedAuromancyModId
import hohserg.advancedauromancy.blocks.BlockWandBuilder.TileWandBuilder
import hohserg.advancedauromancy.blocks._
import hohserg.advancedauromancy.client._
import hohserg.advancedauromancy.client.render.TileWandBuilderSpecialRenderer
import hohserg.advancedauromancy.client.render.simpleItem.SimpleTexturedModelProvider.simpletexturemodel
import hohserg.advancedauromancy.endervisnet.{ClientEnderVisNet, EnderVisNet, ServerEnderVisNet}
import hohserg.advancedauromancy.inventory.{ContainerWandBuilder, GuiWandBuilder}
import hohserg.advancedauromancy.items._
import hohserg.advancedauromancy.items.base.ItemSelfRegister
import hohserg.advancedauromancy.visworld.VisWorldHandler
import hohserg.advancedauromancy.wands.RodsAndCaps
import net.minecraft.block.{Block, BlockContainer}
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.{Item, ItemBlock, ItemStack}
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.network.{IGuiHandler, NetworkRegistry}
import net.minecraftforge.fml.common.registry.{ForgeRegistries, GameRegistry}
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import thaumcraft.api.ThaumcraftApi
import thaumcraft.api.aspects.{Aspect, AspectList}
import thaumcraft.api.research.ResearchCategories

import scala.collection.mutable.ListBuffer

@Mod(name="AdvancedAuromancy",modid = advancedAuromancyModId, version="1.0",modLanguage = "scala",dependencies = "required-after:thaumcraft")
object Main {
  @SidedProxy(clientSide = "hohserg.advancedauromancy.ClientProxy",serverSide = "hohserg.advancedauromancy.ServerProxy")
  var proxy:CommonProxy=_

  final val advancedAuromancyModId="advancedauromancy"

  @Mod.EventHandler def preinit(event: FMLPreInitializationEvent): Unit = {
    proxy.preinit(event)
    NetworkRegistry.INSTANCE.registerGuiHandler(Main, proxy)
    RodsAndCaps.initRodAndCaps()
  }

  @Mod.EventHandler def init(event: FMLInitializationEvent): Unit = {
    proxy.init(event)

  }

  @Mod.EventHandler def postinit(event: FMLPostInitializationEvent): Unit = {
    proxy.postinit(event)
  }
}
class ServerProxy extends CommonProxy{
  override lazy val enderVisNet: EnderVisNet = new ServerEnderVisNet
}

class ClientProxy extends CommonProxy{

  private val itemsSelfRegister=ListBuffer[ItemSelfRegister]()

  override def markForRegisterClient(register: ItemSelfRegister): Unit =
    itemsSelfRegister+=register

  override def preinit(event: FMLPreInitializationEvent): Unit = {
    super.preinit(event)

    PacketCustom.assignHandler(advancedAuromancyModId, new ClientPacketHandler)
    MinecraftForge.EVENT_BUS.register(new ClientEventHandler)
    MinecraftForge.EVENT_BUS.register(new ResearchCatsEditor)
    MinecraftForge.EVENT_BUS.register(new VisWorldHandler.ClientHandler)
    MinecraftForge.EVENT_BUS.register(new TooltipHandler)
    //MinecraftForge.EVENT_BUS.register(new ShaderEventHandler)
  }
  override def init(event: FMLInitializationEvent): Unit = {
    super.init(event)
    //println("test",handler.programID)
    (items++itemsSelfRegister).foreach {
      case item: Item with ModelProvider =>
        val model = item.location
        Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(item, 0, model)
        ModelLoader.setCustomModelResourceLocation(item, 0, model)
      case item =>
        val model = new ModelResourceLocation(item.getRegistryName, "inventory")
        Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(item, 0, model)
        ModelLoader.setCustomModelResourceLocation(item, 0, model)
    }
    blocks.foreach (
      block => {
        val model = new ModelResourceLocation(block.getRegistryName, "inventory")
        Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(Item.getItemFromBlock(block), 0, model)
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, model)
      }
    )
    ClientRegistry.bindTileEntitySpecialRenderer(classOf[BlockWandBuilder.TileWandBuilder], new TileWandBuilderSpecialRenderer)
  }

  override lazy val enderVisNet: EnderVisNet = new ClientEnderVisNet
}

abstract class CommonProxy extends IGuiHandler{

  def enderVisNet:EnderVisNet

  private lazy val tab=new CreativeTabs(advancedAuromancyModId) {
    override def getTabIconItem = new ItemStack(Items.APPLE)
  }

  import Main._

  def markForRegisterClient(register: ItemSelfRegister): Unit = ()


  protected val blocks=ListBuffer[Block](BlockWandBuilder)
  protected val items=ListBuffer[Item](ItemWandCasting,ItemEnderWandCasting,ItemSenseGoggles)

  def preinit(event: FMLPreInitializationEvent): Unit = {
    event.getModMetadata.autogenerated = false
    event.getModMetadata.authorList add "hohserg"

    PacketCustom.assignHandler(advancedAuromancyModId, new ServerPacketHandler)

    items.foreach(item=>{
      val name = nameByClass(item)
      item.setUnlocalizedName(name).setCreativeTab(tab)
      ForgeRegistries.ITEMS.register(if(item.getRegistryName==null)item.setRegistryName(name) else item )
    })
    ForgeRegistries.ITEMS.register(simpletexturemodel)

    blocks.foreach(block=>{
      val name = nameByClass(block)
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

  private def nameByClass(item:Any)= item.getClass.getSimpleName.dropRight(1).toLowerCase

  def init(event: FMLInitializationEvent): Unit = {
  }

  def postinit(event: FMLPostInitializationEvent): Unit = {
    ResearchCategories.registerCategory(advancedAuromancyModId.toUpperCase,"FLUX",
      new AspectList().add(Aspect.AURA,1).add(Aspect.CRAFT,1).add(Aspect.MAGIC,1),
      new ResourceLocation(advancedAuromancyModId,"textures/icon.png"),
      new ResourceLocation(advancedAuromancyModId,"textures/background.png")
    )
    ThaumcraftApi.registerResearchLocation(new ResourceLocation(advancedAuromancyModId, "research/research.json"))


  }
  override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = ID match{
    case 0=>tile(world, x, y, z)
      .map(tile => new GuiWandBuilder(new ContainerWandBuilder(player, tile))).orNull

  }

  override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = ID match{
    case 0=>tile(world, x, y, z)
      .map(new ContainerWandBuilder(player, _)).orNull

  }

  private def tile(world: World, x: Int, y: Int, z: Int): Option[TileWandBuilder] = {
    Option(world.getTileEntity(new BlockPos(x, y, z)))
      .collect({ case tile: TileWandBuilder => tile })
  }
}