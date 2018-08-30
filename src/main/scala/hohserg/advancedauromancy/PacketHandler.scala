package hohserg.advancedauromancy

import codechicken.lib.packet.ICustomPacketHandler.{IClientPacketHandler, IServerPacketHandler}
import codechicken.lib.packet.PacketCustom
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.network.play.{INetHandlerPlayClient, INetHandlerPlayServer}

class ClientPacketHandler extends IClientPacketHandler{

  override def handlePacket(packetCustom: PacketCustom, minecraft: Minecraft, iNetHandlerPlayClient: INetHandlerPlayClient): Unit = {
    packetCustom.getType match {
      case Packet.UpdateVisAmount =>
        val (name, amount, max) = (packetCustom.readString(), packetCustom.readFloat(),  packetCustom.readInt())
        Main.proxy.enderVisNet.setVis(name,amount,max)
      case _ =>
    }

  }
}
class ServerPacketHandler extends IServerPacketHandler{
  override def handlePacket(packetCustom: PacketCustom, player: EntityPlayerMP, iNetHandlerPlayServer: INetHandlerPlayServer): Unit = {
    packetCustom.getType match {
      case _ =>
    }

  }
}

object Packet{
  def createPacket(t:Int):PacketCustom = new PacketCustom(Main.advancedAuromancyModId,t)
  final val UpdateVisAmount = 1
}