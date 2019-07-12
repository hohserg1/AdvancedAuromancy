package hohserg.advancedauromancy.core

import hohserg.advancedauromancy.core.Main.advancedAuromancyModId
import hohserg.advancedauromancy.wands.{WandCap, WandRod, WandUpgrade}
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
      .setDefaultKey(new ResourceLocation(advancedAuromancyModId, "default_cap"))
      .create()
    new RegistryBuilder()
      .setName(new ResourceLocation(advancedAuromancyModId, "wand_rod"))
      .setType(classOf[WandRod])
      .setDefaultKey(new ResourceLocation(advancedAuromancyModId, "default_rod"))
      .create()
    new RegistryBuilder()
      .setName(new ResourceLocation(advancedAuromancyModId, "wand_upgrade"))
      .setType(classOf[WandUpgrade])
      .setDefaultKey(new ResourceLocation(advancedAuromancyModId, "default_upgrade"))
      .create()
  }

}
