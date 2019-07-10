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
  def onTooltip(e:ItemTooltipEvent): Unit ={
    import collection.JavaConverters._
    val stack = e.getItemStack
    stack.getItem match {
      case wand: Wand =>
        if(stack.getItem==ItemEnderWandCasting)
          e.getToolTip add TextFormatting.AQUA+"Ender vis net owner is "+Main.proxy.enderVisNet.getName(stack).getOrElse("")
        val cap = wand.getCap(stack)
        e.getToolTip.set(0, I18n.translateToLocal(cap.name)+" "+I18n.translateToLocal(wand.getRod(stack).name))
        e.getToolTip.set(e.getToolTip.asScala.indexWhere((param: String) => param.contains(visChargeLabel)),TextFormatting.YELLOW+visChargeLabel + " "+wand.getVis(stack))
      case _ =>
    }

  }

}
