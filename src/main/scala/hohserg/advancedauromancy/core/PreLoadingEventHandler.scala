package hohserg.advancedauromancy.core

import hohserg.advancedauromancy.core.Main.advancedAuromancyModId
import hohserg.advancedauromancy.wands.RodsAndCaps.{DefaultCap, DefaultRod}
import hohserg.advancedauromancy.wands.{DefaultMissingFactory, WandCap, WandRod, WandUpgrade}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.registries.RegistryBuilder

@Mod.EventBusSubscriber(modid = Main.advancedAuromancyModId)
object PreLoadingEventHandler {

  @SubscribeEvent def registerWandRegistry(e: RegistryEvent.NewRegistry): Unit = {
    new RegistryBuilder()
      .setName(new ResourceLocation(advancedAuromancyModId, "wand_cap"))
      .setType(classOf[WandCap])
      .set(new DefaultMissingFactory[WandCap](DefaultCap))
      .create()
    new RegistryBuilder()
      .setName(new ResourceLocation(advancedAuromancyModId, "wand_rod"))
      .setType(classOf[WandRod])
      .set(new DefaultMissingFactory[WandRod](DefaultRod))
      .create()
    new RegistryBuilder()
      .setName(new ResourceLocation(advancedAuromancyModId, "wand_upgrade"))
      .setType(classOf[WandUpgrade])
      .set(new DefaultMissingFactory[WandUpgrade](null))
      .create()
  }

}
