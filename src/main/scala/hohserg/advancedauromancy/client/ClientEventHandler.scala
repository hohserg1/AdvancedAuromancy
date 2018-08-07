package hohserg.advancedauromancy.client

import collection.JavaConverters._

import java.lang.reflect.Field
import java.util

import hohserg.advancedauromancy.client.render.wand.WandModel
import hohserg.advancedauromancy.items.ItemWandCasting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.{GuiListWorldSelection, GuiMainMenu, GuiScreen, GuiWorldSelection}
import net.minecraft.client.renderer.block.model.{IBakedModel, ModelResourceLocation}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.{GuiOpenEvent, ModelBakeEvent, TextureStitchEvent}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase
import org.lwjgl.input.Keyboard
import thaumcraft.api.research.ResearchEntry
import thaumcraft.client.gui.GuiResearchBrowser

import scala.collection.mutable.ListBuffer
import scala.util.Try

class ClientEventHandler extends GuiScreen {
  mc = Minecraft.getMinecraft

  private val research=classOf[GuiResearchBrowser].getDeclaredField("research")
  research.setAccessible(true)

  val keys=List((Keyboard.KEY_LEFT,(-1,0)), (Keyboard.KEY_RIGHT,(1,0)), (Keyboard.KEY_DOWN,(0,1)), (Keyboard.KEY_UP,(0,-1)))
  private val currentHighlight=classOf[GuiResearchBrowser].getDeclaredField("currentHighlight")
  currentHighlight.setAccessible(true)
  def getOf[A](field:Field):Option[A]={
    Minecraft.getMinecraft.currentScreen match {
      case browser: GuiResearchBrowser => Option(field.get(browser).asInstanceOf[A])
      case _ => None
    }
  }
  def allResearch: Option[util.LinkedList[ResearchEntry]] = getOf(research)

  def selectedEntry: Option[ResearchEntry] = getOf(currentHighlight)


  @SubscribeEvent
  def onKeyTyped(e:TickEvent.ClientTickEvent): Unit ={
    if(e.phase==Phase.START) {
      if(Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
        val test=allResearch
        allResearch.foreach(
          i => i.asScala.foreach(
            re => ResearchJsonHelper.updateEntry(re)))
      }else
        selectedEntry.foreach(re => keys.find(key => Keyboard.isKeyDown(key._1)).foreach(key => {
          println(re.getDisplayColumn, re.getDisplayColumn + key._2._1)
          re.setDisplayColumn(re.getDisplayColumn + key._2._1)
          re.setDisplayRow(re.getDisplayRow + key._2._2)
        }))
    }

  }

  @SubscribeEvent
  def onMainMenu(event: GuiOpenEvent): Unit =
    event.getGui match {
      case guiMainMenu: GuiMainMenu => mc.displayGuiScreen(new GuiWorldSelection(guiMainMenu))
      case selection: GuiWorldSelection =>
        val guiListWorldSelection = new GuiListWorldSelection(selection, mc, 100, 100, 32, 100 - 64, 36)
        Try(guiListWorldSelection.getListEntry(0).joinWorld())
      case _ =>
    }

  @SubscribeEvent
  def stitcherEventPre(event: TextureStitchEvent.Pre): Unit = ClientEventHandler.forRegister.foreach(event.getMap.registerSprite)


  @SubscribeEvent def onModelBakeEvent(event: ModelBakeEvent): Unit = {
    val model = new ModelResourceLocation(ItemWandCasting.getRegistryName, "inventory")
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
  private val forRegister = new ListBuffer[ResourceLocation]

  def registerTexture(resourceLocation: ResourceLocation): Unit = forRegister += resourceLocation
}
