package hohserg.advancedauromancy.network

import codechicken.lib.packet.PacketCustom
import hohserg.advancedauromancy.core.Main

object Packet {
  def createPacket(t:Int):PacketCustom = new PacketCustom(Main.advancedAuromancyModId,t)
  final val UpdateVisAmount = 1
}
