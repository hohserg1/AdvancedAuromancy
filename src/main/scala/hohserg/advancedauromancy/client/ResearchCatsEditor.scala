package hohserg.advancedauromancy.client

import java.lang.reflect.Field
import java.util

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase
import org.lwjgl.input.Keyboard
import thaumcraft.api.research.ResearchEntry
import thaumcraft.client.gui.{GuiResearchBrowser, GuiResearchPage}

import scala.collection.JavaConverters._

class ResearchCatsEditor extends GuiScreen{
  mc = Minecraft.getMinecraft

  private val research=classOf[GuiResearchBrowser].getDeclaredField("research")
  research.setAccessible(true)

  val keys=List((Keyboard.KEY_LEFT,(-1,0)), (Keyboard.KEY_RIGHT,(1,0)), (Keyboard.KEY_DOWN,(0,1)), (Keyboard.KEY_UP,(0,-1)))


  private val currentHighlight=classOf[GuiResearchBrowser].getDeclaredField("currentHighlight")
  currentHighlight.setAccessible(true)

  def getOf[A](field:Field):Option[A] =
    Option(field.get(Minecraft.getMinecraft.currentScreen).asInstanceOf[A])

  def allResearch: Option[util.LinkedList[ResearchEntry]] = getOf(research)

  def selectedEntry: Option[ResearchEntry] = getOf(currentHighlight)

  @SubscribeEvent
  def onKeyTyped(e: TickEvent.ClientTickEvent): Unit = {
    if (e.phase == Phase.START) {

        Minecraft.getMinecraft.currentScreen match{
          case browser: GuiResearchBrowser =>
            if (Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
              allResearch.foreach(
                i => i.asScala.foreach(
                  re => ResearchJsonHelper.updateEntry(re)))
            }else
              selectedEntry.foreach(re => keys.find(key => Keyboard.isKeyDown(key._1)).foreach(key => {
                re.setDisplayColumn(re.getDisplayColumn + key._2._1)
                re.setDisplayRow(re.getDisplayRow + key._2._2)
              }))
          case page:GuiResearchPage =>
          case _=>

        }

    }

  }
}
