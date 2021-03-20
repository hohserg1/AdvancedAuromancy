package hohserg.advancedauromancy.core

import codechicken.lib.packet.PacketCustom
import hohserg.advancedauromancy.blocks.{BlockOverchargePedestal, BlockWandBuilder}
import hohserg.advancedauromancy.client.render.simpleItem.SimpleTexturedModelProvider
import hohserg.advancedauromancy.client.render.{TileOverchargePedestalSpecialRenderer, TileWandBuilderSpecialRenderer}
import hohserg.advancedauromancy.client.{ClientEventHandler, ModelProvider, ResearchCatsEditor}
import hohserg.advancedauromancy.core.Main.advancedAuromancyModId
import hohserg.advancedauromancy.network.ClientPacketHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPreInitializationEvent}

class ClientProxy extends CommonProxy {

  override def preinit(event: FMLPreInitializationEvent): Unit = {
    super.preinit(event)

    PacketCustom.assignHandler(advancedAuromancyModId, new ClientPacketHandler)
    MinecraftForge.EVENT_BUS.register(new ClientEventHandler)
    MinecraftForge.EVENT_BUS.register(new ResearchCatsEditor)

    wandComponents.foreach(_.registerTexture())
    itemsToRegister.collect { case simpleTexture: SimpleTexturedModelProvider =>
      ClientEventHandler.registerTexture(new ResourceLocation(simpleTexture.textureName))
    }
  }

  override def init(event: FMLInitializationEvent): Unit = {
    super.init(event)
    itemsToRegister.foreach {
      case item: Item with ModelProvider =>
        val model = item.location
        Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(item, 0, model)
        ModelLoader.setCustomModelResourceLocation(item, 0, model)
      case item =>
        val model = new ModelResourceLocation(item.getRegistryName, "inventory")
        Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(item, 0, model)
        ModelLoader.setCustomModelResourceLocation(item, 0, model)
    }
    blocksToRegister.foreach(
      block => {
        val model = new ModelResourceLocation(block.getRegistryName, "inventory")
        Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(Item.getItemFromBlock(block), 0, model)
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, model)
      }
    )
    ClientRegistry.bindTileEntitySpecialRenderer(classOf[BlockWandBuilder.TileWandBuilder], new TileWandBuilderSpecialRenderer)
    ClientRegistry.bindTileEntitySpecialRenderer(classOf[BlockOverchargePedestal.TileOverchargePedestal], new TileOverchargePedestalSpecialRenderer)
  }
}
