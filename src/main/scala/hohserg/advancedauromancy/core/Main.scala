package hohserg.advancedauromancy.core

import hohserg.advancedauromancy.core.Main.advancedAuromancyModId
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.{Mod, SidedProxy}

@Mod(name = "AdvancedAuromancy", modid = advancedAuromancyModId, version = "1.0", modLanguage = "scala", dependencies = "required-after:thaumcraft")
object Main {
  @SidedProxy(clientSide = "hohserg.advancedauromancy.core.ClientProxy", serverSide = "hohserg.advancedauromancy.core.ServerProxy")
  var proxy: CommonProxy = _

  final val advancedAuromancyModId = "advancedauromancy"

  @Mod.EventHandler def preinit(event: FMLPreInitializationEvent): Unit = proxy.preinit(event)


  @Mod.EventHandler def init(event: FMLInitializationEvent): Unit = proxy.init(event)

  @Mod.EventHandler def postinit(event: FMLPostInitializationEvent): Unit = proxy.postinit(event)
}




