package hohserg.advancedauromancy.client.render

import hohserg.advancedauromancy.client.render.wand.WandModel
import hohserg.advancedauromancy.items.ItemWandCasting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.{GuiListWorldSelection, GuiMainMenu, GuiWorldSelection}
import net.minecraft.client.renderer.block.model.{IBakedModel, ModelResourceLocation}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.{GuiOpenEvent, ModelBakeEvent, TextureStitchEvent}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

import scala.collection.mutable.ListBuffer
import scala.util.Try

class ClientEventHandler {
  @SubscribeEvent
  def onMainMenu(event:GuiOpenEvent): Unit =
    event.getGui match {
      case guiMainMenu: GuiMainMenu => Minecraft.getMinecraft.displayGuiScreen(new GuiWorldSelection(guiMainMenu))
      case selection: GuiWorldSelection =>
       val guiListWorldSelection = new GuiListWorldSelection(selection, Minecraft.getMinecraft, 100, 100, 32, 100 - 64, 36)
        Try(guiListWorldSelection.getListEntry(0).joinWorld())
      case _ =>
    }

  @SubscribeEvent
  def stitcherEventPre(event:TextureStitchEvent.Pre): Unit = ClientEventHandler.forRegister.foreach(event.getMap.registerSprite)


  @SubscribeEvent def onModelBakeEvent(event: ModelBakeEvent): Unit = {
    val model=new ModelResourceLocation(ItemWandCasting.getRegistryName, "inventory")
    val `object` = event.getModelRegistry.getObject(model)
    `object` match {
      case existingModel: IBakedModel =>
        val customModel = new WandModel(existingModel)
        event.getModelRegistry.putObject(model, customModel)
      case _ =>
    }
  }
}

object ClientEventHandler {
  private val forRegister=new ListBuffer[ResourceLocation]
  def registerTexture(resourceLocation: ResourceLocation): Unit = forRegister+=resourceLocation
}
