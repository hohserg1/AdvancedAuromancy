package hohserg.advancedauromancy.research

import hohserg.advancedauromancy.core.Main
import hohserg.advancedauromancy.utils.ReflectionUtils.getPrivateField
import net.minecraft.network.NetHandlerPlayServer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import thaumcraft.api.ThaumcraftApi
import thaumcraft.api.items.ItemsTC
import thaumcraft.api.research.{ResearchCategories, ResearchEvent}
import thaumcraft.common.lib.CommandThaumcraftAccess
import thaumcraft.common.lib.network.misc.PacketStartTheoryToServer
import thaumcraft.common.tiles.crafting.TileResearchTable


@Mod.EventBusSubscriber(modid = Main.advancedAuromancyModId)
object ResearchEventHandler {

  @SubscribeEvent(priority = EventPriority.HIGHEST) def onThaumonomiconOpen(e: PlayerInteractEvent.RightClickItem): Unit = {
    if (e.getItemStack.getItem == ItemsTC.thaumonomicon)
      if (e.getEntityPlayer.isSneaking) {
        CommandThaumcraftAccess.reset(e.getEntityPlayer)
        CommandThaumcraftAccess.add(e.getEntityPlayer, "BASEAUROMANCY")
        CommandThaumcraftAccess.add(e.getEntityPlayer, "FLUX")
        CommandThaumcraftAccess.add(e.getEntityPlayer, "!PLANTWOOD")
        ResearchInit.init()
      }
  }

  @SubscribeEvent def replaceResearchKnowledge(e: ResearchEvent.Knowledge): Unit =
    if (e.getCategory == ResearchInit.advancedAuromancyCategory) {
      e.setCanceled(true)
      ThaumcraftApi.internalMethods.addKnowledge(e.getPlayer, e.getType, ResearchCategories.getResearchCategory("AUROMANCY"), e.getAmount)
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
        tile.data.categoriesBlocked.add(ResearchInit.advancedAuromancyCategory.key)
      case _ =>
    }
  }

}
