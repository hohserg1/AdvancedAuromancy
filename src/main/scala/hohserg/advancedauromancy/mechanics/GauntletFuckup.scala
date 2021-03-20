package hohserg.advancedauromancy.mechanics

import hohserg.advancedauromancy.core.Main
import hohserg.advancedauromancy.items.base.Wand
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import thaumcraft.common.items.casters.ItemCaster

@EventBusSubscriber(modid = Main.advancedAuromancyModId)
object GauntletFuckup {

  @SubscribeEvent
  def onUseGauntlet(e: PlayerInteractEvent.RightClickItem): Unit = {
    e.getItemStack.getItem match {
      case _: Wand =>
      case caster: ItemCaster =>
        println(e.getHand, e.getPos, e.getResult)
      case _ =>

    }
  }

}
