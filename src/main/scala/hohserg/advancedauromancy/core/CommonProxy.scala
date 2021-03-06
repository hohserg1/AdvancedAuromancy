package hohserg.advancedauromancy.core

import codechicken.lib.packet.PacketCustom
import hohserg.advancedauromancy.api._
import hohserg.advancedauromancy.blocks.BlockWandBuilder.TileWandBuilder
import hohserg.advancedauromancy.blocks.{BlockOverchargePedestal, BlockWandBuilder}
import hohserg.advancedauromancy.client.render.simpleItem.SimpleTexturedModelProvider.simpletexturemodel
import hohserg.advancedauromancy.core.Main._
import hohserg.advancedauromancy.endervisnet.EnderVisNet
import hohserg.advancedauromancy.foci.FocusMediumOrb
import hohserg.advancedauromancy.inventory.{ContainerWandBuilder, GuiWandBuilder}
import hohserg.advancedauromancy.items._
import hohserg.advancedauromancy.items.charms.{HonedCharm, ImprovedCharm}
import hohserg.advancedauromancy.items.misc.{GoldPlate, SingularCrystal, ThaumiumInnertCap, VoidInnertCap}
import hohserg.advancedauromancy.network.ServerPacketHandler
import hohserg.advancedauromancy.research.{ResearchEventHandler, ResearchInit}
import hohserg.advancedauromancy.utils.ReflectionUtils._
import hohserg.advancedauromancy.wands._
import it.unimi.dsi.fastutil.objects.Object2ByteMap
import net.minecraft.block.{Block, ITileEntityProvider}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.{Item, ItemBlock, ItemStack}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.simpleimpl._
import net.minecraftforge.fml.common.network.{FMLIndexedMessageToMessageCodec, IGuiHandler, NetworkRegistry}
import net.minecraftforge.fml.common.registry.{EntityEntry, GameRegistry}
import net.minecraftforge.fml.relauncher.Side
import thaumcraft.api.casters.FocusEngine
import thaumcraft.api.crafting.IDustTrigger
import thaumcraft.common.lib.crafting.WandBuilderCraft
import thaumcraft.common.lib.network.PacketHandler
import thaumcraft.common.lib.network.misc.PacketStartTheoryToServer

abstract class CommonProxy extends IGuiHandler {

  lazy val tab = new CreativeTabs(advancedAuromancyModId) {
    override def getTabIconItem = new ItemStack(Items.APPLE)
  }

  def prepareBlocks(blocks: Block*): Seq[Block] = {
    blocks.foreach { block =>
      val name = nameByClass(block)
      block
        .setUnlocalizedName(name)
        .setRegistryName(name)
        .setCreativeTab(tab)
    }
    blocks
  }

  def prepareItems(items: Seq[Item]): Seq[Item] = {
    items.foreach { item =>
      val alreadyHaveName = item.getRegistryName != null
      val name = if (alreadyHaveName) item.getRegistryName.getResourcePath else nameByClass(item)
      item
        .setUnlocalizedName(name)
        .setCreativeTab(tab)
      if (!alreadyHaveName)
        item.setRegistryName(name)
    }
    items
  }

  def prepareTiles(blocks: Seq[Block]): Seq[Class[_ <: TileEntity]] = {
    blocks.collect {
      case container: ITileEntityProvider =>
        container.createNewTileEntity(null, 0).getClass
    }
  }

  def prepareWandComponents(components: Seq[WandComponentRegistryEntry[_]]): Seq[WandComponentRegistryEntry[_]] = {
    components.foreach { c =>
      if (c.getRegistryName == null)
        c.setRegistryName(nameByClass(c))
    }
    components
  }


  protected lazy val wandComponents = prepareWandComponents(AACaps.values ++ AARods.values ++ AACapUpgrades.values ++ AARodUpgrades.values ++ AAResonators.values)

  protected lazy val blocksToRegister = prepareBlocks(BlockWandBuilder, BlockOverchargePedestal)
  protected lazy val itemsToRegister = prepareItems(
    Seq(ItemWandCasting, ItemEnderWandCasting, GoldPlate, ImprovedCharm, HonedCharm, VoidInnertCap, ThaumiumInnertCap, SingularCrystal, simpletexturemodel) ++
      blocksToRegister.map(block => new ItemBlock(block).setRegistryName(block.getRegistryName)) ++
      wandComponents.filter(_.useRegularItemRepresent).flatMap(c => {
        try {
          Seq(c.item.setRegistryName(c.getRegistryName))
        } catch {
          case e: NullPointerException =>
            println("Item of " + c + " is null. It's a bug!")
            Seq()
        }
      })
  )

  protected lazy val tilesToRegister = prepareTiles(blocksToRegister)
  protected lazy val entityToRegister = List[EntityEntry]()

  def preinit(event: FMLPreInitializationEvent): Unit = {
    event.getModMetadata.autogenerated = false
    event.getModMetadata.authorList add "hohserg"

    MinecraftForge.EVENT_BUS.register(this)
    MinecraftForge.EVENT_BUS.register(EnderVisNet.eventHandler)

    PacketCustom.assignHandler(advancedAuromancyModId, new ServerPacketHandler)

    NetworkRegistry.INSTANCE.registerGuiHandler(Main, this)

    addThaumcraftPacketHandler()
  }

  def addThaumcraftPacketHandler(): Unit = {
    val packetCodec = getPrivateField[SimpleNetworkWrapper, SimpleIndexedCodec](PacketHandler.INSTANCE, "packetCodec")
    val types = getPrivateField[FMLIndexedMessageToMessageCodec[IMessage], Object2ByteMap[Class[_ <: IMessage]]](packetCodec, "types")
    val id = types.get(classOf[PacketStartTheoryToServer])

    PacketHandler.INSTANCE.registerMessage[PacketStartTheoryToServer, IMessage](
      new ResearchEventHandler.PacketStartTheoryToServerHandler, classOf[PacketStartTheoryToServer], id.toInt, Side.SERVER)
  }


  private def nameByClass(item: Any): String = item.getClass.getSimpleName.dropRight(1).flatMap((i: Char) => if (i.isUpper) "_" + i.toLower else "" + i).drop(1)

  @SubscribeEvent def registerBlocks(e: RegistryEvent.Register[Block]): Unit = {
    blocksToRegister.foreach(e.getRegistry.register)
    tilesToRegister.foreach(tile => GameRegistry.registerTileEntity(tile, new ResourceLocation(advancedAuromancyModId, tile.getSimpleName)))
  }

  @SubscribeEvent def registerItems(e: RegistryEvent.Register[Item]): Unit = {
    println("registerItems")
    itemsToRegister.foreach(e.getRegistry.register)
  }

  val prefix = advancedAuromancyModId + ":rods_and_caps/"

  @SubscribeEvent def registerWandCap(e: RegistryEvent.Register[WandCap]): Unit =
    AACaps.values.foreach(e.getRegistry.register)

  @SubscribeEvent def registerWandRod(e: RegistryEvent.Register[WandRod]): Unit =
    AARods.values.foreach(e.getRegistry.register)

  @SubscribeEvent def registerCapUpgrade(e: RegistryEvent.Register[CapUpgrade]): Unit =
    AACapUpgrades.values.foreach(e.getRegistry.register)

  @SubscribeEvent def registerRodUpgrade(e: RegistryEvent.Register[RodUpgrade]): Unit =
    AARodUpgrades.values.foreach(e.getRegistry.register)

  @SubscribeEvent def registerScepterResonators(e: RegistryEvent.Register[ScepterResonator]): Unit =
    AAResonators.values.foreach(e.getRegistry.register)


  @SubscribeEvent def registerEntities(e: RegistryEvent.Register[EntityEntry]): Unit =
    entityToRegister.foreach(e.getRegistry.register)

  def init(event: FMLInitializationEvent): Unit = {
    FocusEngine.registerElement(classOf[FocusMediumOrb], new ResourceLocation(advancedAuromancyModId, "textures/foci/projectile.png"), 11382149)
    IDustTrigger.registerDustTrigger(new WandBuilderCraft)
  }

  def postinit(event: FMLPostInitializationEvent): Unit = {
    ResearchInit.init()
    //ThaumcraftApi.registerResearchLocation(new ResourceLocation(advancedAuromancyModId, "research/research.json"))
  }


  override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = ID match {
    case 0 => tile(world, x, y, z)
      .map(tile => new GuiWandBuilder(new ContainerWandBuilder(player, tile))).orNull

  }

  override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = ID match {
    case 0 => tile(world, x, y, z)
      .map(new ContainerWandBuilder(player, _)).orNull

  }

  private def tile(world: World, x: Int, y: Int, z: Int): Option[TileWandBuilder] = {
    Option(world.getTileEntity(new BlockPos(x, y, z)))
      .collect({ case tile: TileWandBuilder => tile })
  }
}
