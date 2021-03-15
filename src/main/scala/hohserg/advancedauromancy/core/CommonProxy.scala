package hohserg.advancedauromancy.core

import baubles.api.BaublesApi
import codechicken.lib.packet.PacketCustom
import hohserg.advancedauromancy.blocks.BlockWandBuilder.TileWandBuilder
import hohserg.advancedauromancy.blocks.{BlockOverchargePedestal, BlockWandBuilder}
import hohserg.advancedauromancy.client.render.simpleItem.SimpleTexturedModelProvider.simpletexturemodel
import hohserg.advancedauromancy.core.Main._
import hohserg.advancedauromancy.endervisnet.EnderVisNet
import hohserg.advancedauromancy.foci.FocusMediumOrb
import hohserg.advancedauromancy.inventory.{ContainerWandBuilder, GuiWandBuilder}
import hohserg.advancedauromancy.items._
import hohserg.advancedauromancy.items.base.Wand
import hohserg.advancedauromancy.items.charms.ImprovedCharm
import hohserg.advancedauromancy.network.ServerPacketHandler
import hohserg.advancedauromancy.wands.RodsAndCaps._
import hohserg.advancedauromancy.wands.WandRod.{apply => _, _}
import hohserg.advancedauromancy.wands.{CapUpgrade, RodUpgrade, WandCap, WandRod}
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
import thaumcraft.api.items.{IRechargable, RechargeHelper}
import thaumcraft.api.research.ResearchCategories
import thaumcraft.common.items.casters.ItemFocus
import thaumcraft.common.world.aura.AuraHandler

import scala.collection.mutable.ListBuffer

abstract class CommonProxy extends IGuiHandler {

  lazy val tab = new CreativeTabs(advancedAuromancyModId) {
    override def getTabIconItem = new ItemStack(Items.APPLE)
  }


  protected val blocksToRegister = ListBuffer[Block](BlockWandBuilder, BlockOverchargePedestal)
  protected val itemsToRegister = ListBuffer[Item](ItemWandCasting, ItemEnderWandCasting, GoldPlate, ImprovedCharm, HonedCharm, ItemWandComponent.ItemCap, ItemWandComponent.ItemRod, ItemWandComponent.ItemCapUpgrade, ItemWandComponent.ItemRodUpgrade, simpletexturemodel)
  protected val tilesToRegister = ListBuffer[Class[_ <: TileEntity]]()
  protected val entityToRegister = ListBuffer[EntityEntry]()

  def preinit(event: FMLPreInitializationEvent): Unit = {
    event.getModMetadata.autogenerated = false
    event.getModMetadata.authorList add "hohserg"

    MinecraftForge.EVENT_BUS.register(this)
    MinecraftForge.EVENT_BUS.register(EnderVisNet.eventHandler)

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
      GoldCap,
      ThaumiumCap,
      VoidCap,
      AuramCap,
      EnderCap,
      DefaultCap
    )
    ItemWandComponent.loadTexturesFor(e.getRegistry)
  }

  @SubscribeEvent def registerWandRod(e: RegistryEvent.Register[WandRod]): Unit = {
    e.getRegistry.registerAll(
      GreatwoodRod,
      SilverwoodRod,
      TaintwoodRod,

      BirchRod,
      OakRod,
      SpruceRod,
      JungleRod,
      DefaultRod
    )
    ItemWandComponent.loadTexturesFor(e.getRegistry)
  }

  @SubscribeEvent def registerCapUpgrade(e: RegistryEvent.Register[CapUpgrade]): Unit = {
    def elementalPlatingOf(aspect: Aspect) = {
      CapUpgrade("elemental_plating_" + aspect.getTag,
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
        }, 50, identityOnUpdate
      )()
    }


    e.getRegistry.registerAll(
      elementalPlatingOf(AIR),
      elementalPlatingOf(EARTH),
      elementalPlatingOf(ENTROPY),
      elementalPlatingOf(ORDER),
      elementalPlatingOf(FIRE),
      elementalPlatingOf(COLD),
      DefaultCapUpgrade
    )
  }

  @SubscribeEvent def registerRodUpgrade(e: RegistryEvent.Register[RodUpgrade]): Unit = {

    def itemsView(size: Int, stackByIndex: Int => ItemStack) =
      (0 until size) map stackByIndex


    e.getRegistry.registerAll(
      RodUpgrade("capacity_intercalation", 50, 100, identityOnUpdate)(),
      RodUpgrade("vis_absorption", 0, 100, (stack, player) =>
        if (player.world.rand.nextInt(100) == 0)
          Wand.wand(stack) {
            wand => wand.addVis(stack, AuraHandler.drainVis(player.world, player.getPosition, 1, false))
          }
      )(),
      RodUpgrade("inventory_charger", 0, 100, (stack, player) =>
        if (!player.world.isRemote && player.ticksExisted % 40 == 0)
          Wand.wand(stack) {
            wand =>
              if (wand.getVis(stack) >= 1) {
                val baubles = BaublesApi.getBaublesHandler(player)
                val armor = player.inventory.armorInventory
                val itemsToCharge =
                  itemsView(player.inventory.getSizeInventory, player.inventory.getStackInSlot) ++
                    itemsView(baubles.getSlots, baubles.getStackInSlot) ++
                    itemsView(armor.size(), armor.get)

                val canBeChargered = itemsToCharge
                  .map(i => i -> i.getItem)
                  .collectFirst { case (itemStack, item: IRechargable) if itemStack != stack && item.getMaxCharge(itemStack, player) - RechargeHelper.getCharge(itemStack) >= 1 =>
                    itemStack
                  }

                canBeChargered.foreach { i =>
                  val chargered = RechargeHelper.rechargeItemBlindly(i, player, 1)
                  wand.setVis(stack, wand.getVis(stack) - chargered)
                }
              }
          }
      )(),
      ChargeIndicator,
      DefaultRodUpgrade
    )
    ItemWandComponent.loadTexturesFor(e.getRegistry)
  }

  val f = true

  @SubscribeEvent def registerEntities(e: RegistryEvent.Register[EntityEntry]): Unit = {
    entityToRegister.foreach(e.getRegistry.register)
  }

  def init(event: FMLInitializationEvent): Unit = {
    FocusEngine.registerElement(classOf[FocusMediumOrb], new ResourceLocation(advancedAuromancyModId, "textures/foci/projectile.png"), 11382149)
    IDustTrigger.registerDustTrigger(new WandBuilderCraft)
  }

  lazy val thaumonomiconCategory = ResearchCategories.registerCategory(advancedAuromancyModId.toUpperCase, "FLUX",
    new AspectList().add(AURA, 1).add(CRAFT, 1).add(MAGIC, 1),
    new ResourceLocation(advancedAuromancyModId, "textures/icon.png"),
    new ResourceLocation(advancedAuromancyModId, "textures/background.png")
  )

  def postinit(event: FMLPostInitializationEvent): Unit = {
    println(thaumonomiconCategory)
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
