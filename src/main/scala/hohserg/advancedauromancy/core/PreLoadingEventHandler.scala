package hohserg.advancedauromancy.core

import hohserg.advancedauromancy.core.Main.advancedAuromancyModId
import hohserg.advancedauromancy.wands.{CapUpgrade, RodUpgrade, WandCap, WandRod}
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
      .setName(new ResourceLocation(advancedAuromancyModId, "rod_upgrade"))
      .setType(classOf[RodUpgrade])
      .setDefaultKey(new ResourceLocation(advancedAuromancyModId, "default_rod_upgrade"))
      .create()
    new RegistryBuilder()
      .setName(new ResourceLocation(advancedAuromancyModId, "cap_upgrade"))
      .setType(classOf[CapUpgrade])
      .setDefaultKey(new ResourceLocation(advancedAuromancyModId, "default_cap_upgrade"))
      .create()
  }

}
