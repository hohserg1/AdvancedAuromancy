package hohserg.advancedauromancy.core

import codechicken.lib.packet.PacketCustom
import hohserg.advancedauromancy.blocks.BlockWandBuilder
import hohserg.advancedauromancy.blocks.BlockWandBuilder.TileWandBuilder
import hohserg.advancedauromancy.client.render.simpleItem.SimpleTexturedModelProvider.simpletexturemodel
import hohserg.advancedauromancy.core.Main._
import hohserg.advancedauromancy.endervisnet.EnderVisNet
import hohserg.advancedauromancy.foci.FocusMediumOrb
import hohserg.advancedauromancy.inventory.{ContainerWandBuilder, GuiWandBuilder}
import hohserg.advancedauromancy.items._
import hohserg.advancedauromancy.network.ServerPacketHandler
import hohserg.advancedauromancy.wands.RodsAndCaps.{DefaultCap, DefaultRod, DefaultUpgrade}
import hohserg.advancedauromancy.wands.WandRod.identityOnUpdate
import hohserg.advancedauromancy.wands.{WandCap, WandRod, WandUpgrade}
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
import net.minecraftforge.fml.common.network.{IGuiHandler, NetworkRegistry}
import net.minecraftforge.fml.common.registry.{EntityEntry, GameRegistry}
import thaumcraft.api.ThaumcraftApi
import thaumcraft.api.aspects.Aspect._
import thaumcraft.api.aspects.{Aspect, AspectList}
import thaumcraft.api.casters.FocusEngine
import thaumcraft.api.crafting.IDustTrigger
import thaumcraft.api.research.ResearchCategories
import thaumcraft.common.items.casters.ItemFocus

import scala.collection.mutable.ListBuffer

abstract class CommonProxy extends IGuiHandler {

  def enderVisNet: EnderVisNet

  lazy val tab = new CreativeTabs(advancedAuromancyModId) {
    override def getTabIconItem = new ItemStack(Items.APPLE)
  }


  protected val blocksToRegister = ListBuffer[Block](BlockWandBuilder)
  protected val itemsToRegister = ListBuffer[Item](ItemWandCasting, ItemEnderWandCasting, ItemWandComponent, simpletexturemodel)
  protected val tilesToRegister = ListBuffer[Class[_ <: TileEntity]]()
  protected val entityToRegister = ListBuffer[EntityEntry]()

  def preinit(event: FMLPreInitializationEvent): Unit = {
    event.getModMetadata.autogenerated = false
    event.getModMetadata.authorList add "hohserg"

    MinecraftForge.EVENT_BUS.register(this)

    PacketCustom.assignHandler(advancedAuromancyModId, new ServerPacketHandler)

    itemsToRegister.foreach(item => {
      val name = nameByClass(item)
      item
        .setUnlocalizedName(name)
        .setCreativeTab(tab)
      if (item.getRegistryName == null)
        item.setRegistryName(name)
    })

    blocksToRegister.foreach(block => {
      val name = nameByClass(block)
      block
        .setUnlocalizedName(name)
        .setRegistryName(name)
        .setCreativeTab(tab)
      itemsToRegister += new ItemBlock(block).setRegistryName(name)
      block match {
        case container: ITileEntityProvider =>
          tilesToRegister += container.createNewTileEntity(null, 0).getClass
        case _ =>
      }
    })

    NetworkRegistry.INSTANCE.registerGuiHandler(Main, this)
  }

  private def nameByClass(item: Any) = item.getClass.getSimpleName.dropRight(1).toLowerCase

  @SubscribeEvent def registerBlocks(e: RegistryEvent.Register[Block]): Unit = {
    blocksToRegister.foreach(e.getRegistry.register)
    tilesToRegister.foreach(tile => GameRegistry.registerTileEntity(tile, new ResourceLocation(advancedAuromancyModId, tile.getSimpleName)))
  }

  @SubscribeEvent def registerItems(e: RegistryEvent.Register[Item]): Unit = {
    itemsToRegister.foreach(e.getRegistry.register)
  }

  val prefix = advancedAuromancyModId + ":rods_and_caps/"

  @SubscribeEvent def registerWandCap(e: RegistryEvent.Register[WandCap]): Unit = {
    e.getRegistry.registerAll(
      WandCap("gold_cap", 30, 100)(),
      WandCap("thaumium_cap", 30, 100)(),
      WandCap("void_cap", 30, 100)(),
      WandCap("auram_cap", 30, 100)(),
      WandCap("ender_cap", 30, 100)(),
      DefaultCap
    )
    ItemWandComponent.loadTexturesFor(e.getRegistry)
  }

  @SubscribeEvent def registerWandRod(e: RegistryEvent.Register[WandRod]): Unit = {
    e.getRegistry.registerAll(
      WandRod("greatwood_rod", 100, 0, identityOnUpdate)(),
      WandRod("silverwood_rod", 100, 0, identityOnUpdate)(),
      WandRod("taintwood_rod", 100, 0, identityOnUpdate)(),

      WandRod("birch_rod", 100, 0, identityOnUpdate)(),
      WandRod("oak_rod", 100, 0, identityOnUpdate)(),
      WandRod("spruce_rod", 100, 0, identityOnUpdate)(),
      WandRod("jungle_rod", 100, 0, identityOnUpdate)(),
      DefaultRod
    )
    ItemWandComponent.loadTexturesFor(e.getRegistry)
  }

  @SubscribeEvent def registerWandUpgrade(e: RegistryEvent.Register[WandUpgrade]): Unit = {
    def elementalPlatingOf(aspect: Aspect) = {
      WandUpgrade("elemental_plating_" + aspect.getTag,
        0,
        (stack, player, crafting) => {
          if (crafting)
            0
          else
            ItemWandCasting.getFocusStackOption(stack)
              .map(ItemFocus.getPackage)
              .map(_.getFocusEffects.map(_.getAspect))
              .map(aspects =>
                5 * aspects.count(_ == aspect) / aspects.length
              ).getOrElse(0)
        }, 50, WandRod.identityOnUpdate
      )
    }

    e.getRegistry.registerAll(
      elementalPlatingOf(AIR),
      elementalPlatingOf(EARTH),
      elementalPlatingOf(ENTROPY),
      elementalPlatingOf(ORDER),
      elementalPlatingOf(FIRE),
      elementalPlatingOf(COLD),
      DefaultUpgrade
    )
    ItemWandComponent.loadTexturesFor(e.getRegistry)
  }

  @SubscribeEvent def registerEntities(e: RegistryEvent.Register[EntityEntry]): Unit = {
    entityToRegister.foreach(e.getRegistry.register)
  }

  def init(event: FMLInitializationEvent): Unit = {
    FocusEngine.registerElement(classOf[FocusMediumOrb], new ResourceLocation(advancedAuromancyModId, "textures/foci/projectile.png"), 11382149)
    IDustTrigger.registerDustTrigger(new WandBuilderCraft)
  }

  def postinit(event: FMLPostInitializationEvent): Unit = {
    ResearchCategories.registerCategory(advancedAuromancyModId.toUpperCase, "FLUX",
      new AspectList().add(AURA, 1).add(CRAFT, 1).add(MAGIC, 1),
      new ResourceLocation(advancedAuromancyModId, "textures/icon.png"),
      new ResourceLocation(advancedAuromancyModId, "textures/background.png")
    )
    ThaumcraftApi.registerResearchLocation(new ResourceLocation(advancedAuromancyModId, "research/research.json"))
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
