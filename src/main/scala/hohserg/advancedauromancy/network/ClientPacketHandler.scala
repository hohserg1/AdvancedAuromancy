package hohserg.advancedauromancy.network

import codechicken.lib.packet.ICustomPacketHandler.IClientPacketHandler
import codechicken.lib.packet.PacketCustom
import hohserg.advancedauromancy.core.Main
import net.minecraft.client.Minecraft
import net.minecraft.network.play.INetHandlerPlayClient

class ClientPacketHandler extends IClientPacketHandler{

  override def handlePacket(packetCustom: PacketCustom, minecraft: Minecraft, iNetHandlerPlayClient: INetHandlerPlayClient): Unit = {
    packetCustom.getType match {
      case Packet.UpdateVisAmount =>
        val (name, amount, max) = (packetCustom.readString(), packetCustom.readFloat(),  packetCustom.readInt())
        EnderVisNet.setVis(name,amount,max)
      case _ =>
    }

  }
}
