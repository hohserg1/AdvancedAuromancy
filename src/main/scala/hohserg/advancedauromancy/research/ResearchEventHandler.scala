package hohserg.advancedauromancy.research

import hohserg.advancedauromancy.core.Main
import hohserg.advancedauromancy.utils.ReflectionUtils.getPrivateField
import net.minecraft.network.NetHandlerPlayServer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import thaumcraft.api.ThaumcraftApi
import thaumcraft.api.research.{ResearchCategories, ResearchEvent}
import thaumcraft.common.lib.network.misc.PacketStartTheoryToServer
import thaumcraft.common.tiles.crafting.TileResearchTable


@Mod.EventBusSubscriber(modid = Main.advancedAuromancyModId)
object ResearchEventHandler {

  @SubscribeEvent def replaceResearchKnowledge(e: ResearchEvent.Knowledge): Unit = {
    if (e.getCategory == Main.proxy.thaumonomiconCategory) {
      e.setCanceled(true)
      ThaumcraftApi.internalMethods.addKnowledge(e.getPlayer, e.getType, ResearchCategories.getResearchCategory("AUROMANCY"), e.getAmount)
    }
  }

  class PacketStartTheoryToServerHandler extends IMessageHandler[PacketStartTheoryToServer, IMessage] {
    override def onMessage(message: PacketStartTheoryToServer, ctx: MessageContext): IMessage = {
      val r = message.onMessage(message, ctx)

      val playServer = ctx.netHandler.asInstanceOf[NetHandlerPlayServer]
      val player = playServer.player
      val world = playServer.player.getServerWorld
      val bp = BlockPos.fromLong(getPrivateField[PacketStartTheoryToServer, Long](message, "pos"))
      world.addScheduledTask(new Runnable() {
        override def run(): Unit = {
          addBlockedCategoryToResearchTable(world.getTileEntity(bp))
        }
      })

      r
    }
  }

  private def addBlockedCategoryToResearchTable(someTile: TileEntity) = {
    someTile match {
      case tile: TileResearchTable =>
        tile.data.categoriesBlocked.add(Main.proxy.thaumonomiconCategory.key)
      case _ =>
    }
  }

}
