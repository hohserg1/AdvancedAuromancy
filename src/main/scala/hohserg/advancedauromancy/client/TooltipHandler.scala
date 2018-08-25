package hohserg.advancedauromancy.client

import hohserg.advancedauromancy.items.base.Wand
import net.minecraft.util.text.translation.I18n
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}

class TooltipHandler {
  @SubscribeEvent(priority = EventPriority.LOWEST)
  def onTooltip(e:ItemTooltipEvent): Unit ={
    import collection.JavaConverters._
    val stack = e.getItemStack
    stack.getItem match {
      case wand: Wand =>
        //if(stack.getItem==ItemEnderWandCasting)
       //   e.getToolTip add "§bEnder vis net owner is "+EnderVisNetServer.getName(stack).getOrElse("")
        e.getToolTip.set(0, I18n.translateToLocal(wand.getCap(stack).name)+" "+I18n.translateToLocal(wand.getRod(stack).name))
        e.getToolTip.set(e.getToolTip.asScala.indexWhere((param: String) => param.contains("Vis charge")),"Vis charge: "+wand.getVis(stack))
      case _ =>
    }

  }

}
