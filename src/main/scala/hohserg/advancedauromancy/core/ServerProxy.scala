package hohserg.advancedauromancy.core

import hohserg.advancedauromancy.endervisnet.ServerEnderVisNet
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

class ServerProxy extends CommonProxy {

  override lazy val enderVisNet = new ServerEnderVisNet

  override def preinit(event: FMLPreInitializationEvent): Unit = {
    MinecraftForge.EVENT_BUS.register(enderVisNet.worlsSaveHandler)
  }

}
