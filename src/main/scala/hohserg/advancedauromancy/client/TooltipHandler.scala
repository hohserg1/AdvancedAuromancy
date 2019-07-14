package hohserg.advancedauromancy.client

import hohserg.advancedauromancy.core.Main
import hohserg.advancedauromancy.items.ItemEnderWandCasting
import hohserg.advancedauromancy.items.base.Wand
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.translation.I18n
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}

class TooltipHandler {
  lazy val visChargeLabel = I18n.translateToLocal("tc.charge")

  @SubscribeEvent(priority = EventPriority.LOWEST)
  def onTooltip(e: ItemTooltipEvent): Unit = {
    import collection.JavaConverters._
    val stack = e.getItemStack
    stack.getItem match {
      case wand: Wand =>
        if (stack.getItem == ItemEnderWandCasting)
          e.getToolTip add TextFormatting.AQUA + "Ender vis net owner is " + Main.proxy.enderVisNet.getName(stack).getOrElse("")
        e.getToolTip.set(0, I18n.translateToLocal(wand.getCap(stack).name) + " " + I18n.translateToLocal(wand.getRod(stack).name))
        e.getToolTip.set(e.getToolTip.asScala.indexWhere((param: String) => param.contains(visChargeLabel)), TextFormatting.YELLOW + visChargeLabel + " " + wand.getVis(stack))
        e.getToolTip.add("Avarage crafting vis cost is " + wand.getConsumptionModifier(stack, e.getEntityPlayer, crafting = true))
        e.getToolTip.add("Avarage casting vis cost is " + wand.getConsumptionModifier(stack, e.getEntityPlayer, crafting = false))
        e.getToolTip.add("Upgrades: " + wand.getUpgrades(stack))
      case _ =>
    }

  }

}
