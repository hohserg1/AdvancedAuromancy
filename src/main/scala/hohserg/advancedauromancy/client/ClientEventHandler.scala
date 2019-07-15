package hohserg.advancedauromancy.client

import java.awt.Color
import java.text.DecimalFormat

import hohserg.advancedauromancy.client.render.simpleItem.{SimpleTexturedModelProvider, TexturedModel}
import hohserg.advancedauromancy.client.render.wand.WandModel
import hohserg.advancedauromancy.core.Main
import hohserg.advancedauromancy.items.ItemWandCasting
import hohserg.advancedauromancy.items.base.Wand
import net.minecraft.client.Minecraft
import net.minecraft.client.gui._
import net.minecraft.client.renderer.block.model.{IBakedModel, ModelResourceLocation}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.{GuiOpenEvent, ModelBakeEvent, TextureStitchEvent}
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11._
import thaumcraft.api.aspects.Aspect
import thaumcraft.client.lib.UtilsFX

import scala.collection.mutable.ListBuffer
import scala.util.Try

class ClientEventHandler extends GuiScreen {
  mc = Minecraft.getMinecraft

  @SubscribeEvent
  def renderTick(event: TickEvent.RenderTickEvent) {
    if (event.phase != Phase.START && mc.inGameHasFocus)
      mc.getRenderViewEntity match {
        case player: EntityPlayer =>
          val handStack = player.getHeldItemMainhand
          if (!handStack.isEmpty && handStack.getItem.isInstanceOf[Wand])
            renderCastingWandHud(handStack)
        case _ =>
      }
  }

  lazy val particleTexture = new ResourceLocation(Main.advancedAuromancyModId, "textures/particle/particle.png")


  lazy val hudTexture = new ResourceLocation("thaumcraft", "textures/gui/hud.png")

  val visCountFormat = new DecimalFormat("#######.#")

  def renderCastingWandHud(wandstack: ItemStack): Unit = {
    wandstack.getItem match {
      case wand: Wand =>

        glPushMatrix()

        val sr = new ScaledResolution(Minecraft.getMinecraft)
        glClear(256)
        glMatrixMode(5889)
        glLoadIdentity()
        glOrtho(0.0D, sr.getScaledWidth_double, sr.getScaledHeight_double, 0.0D, 1000.0D, 3000.0D)
        glMatrixMode(5888)
        glEnable(3042)
        glBlendFunc(770, 771)

        //GL11.glTranslatef(16.0F, 16.0F, 0.0F)
        mc.renderEngine.bindTexture(hudTexture)
        val max = wand.getMaxVis(wandstack)
        val cur = wand.getVis(wandstack)

        glTranslatef(42, 6, 0.0F)
        glScaled(0.5D, 0.5D, 0.5D)
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F)

        val loc = (30.0F * cur / max).toInt

        glPushMatrix()
        val ac = new Color(Aspect.AURA.getColor)
        glColor4f(ac.getRed / 255.0F, ac.getGreen / 255.0F, ac.getBlue / 255.0F, 0.8F)
        UtilsFX.drawTexturedQuad(-4.0F, 35 - loc, 104.0F, 0.0F, 8.0F, loc, -90.0D)
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
        glPopMatrix()

        glPushMatrix()
        UtilsFX.drawTexturedQuad(-8.0F, -3.0F, 72.0F, 0.0F, 16.0F, 42.0F, -90.0D)
        glPopMatrix()

        glColor4f(1, 1, 1, 1)

        glPopMatrix()

        GL11.glDisable(3042)

        if (mc.player.isSneaking) {
          GL11.glPushMatrix()
          GL11.glRotatef(-90, 0, 0, 1)
          mc.ingameGUI.drawString(mc.fontRenderer, visCountFormat format wand.getVis(wandstack), -32, -4, 16777215)
          GL11.glPopMatrix()
        }
      case _ =>

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

  @SubscribeEvent(priority = EventPriority.LOW)
  def stitcherEventPre(event: TextureStitchEvent.Pre): Unit = {
    ClientEventHandler.forRegister.foreach(event.getMap.registerSprite)
  }

  val modelWrappersMap = Map(
    new ModelResourceLocation(ItemWandCasting.getRegistryName, "inventory") -> {
      new WandModel(_)
    },
    SimpleTexturedModelProvider.defaultLocation -> {
      new TexturedModel(_)
    }
  )

  @SubscribeEvent def onModelBakeEvent(event: ModelBakeEvent): Unit = {
    modelWrappersMap.foreach { case (model, wrapper) =>
      val `object` = event.getModelRegistry.getObject(model)
      `object` match {
        case existingModel: IBakedModel =>
          val customModel = wrapper(existingModel)
          event.getModelRegistry.putObject(model, customModel)
        case _ =>
      }
    }
  }
}

object ClientEventHandler {
  private val forRegister = new ListBuffer[ResourceLocation]

  def registerTexture(resourceLocation: ResourceLocation): Unit = forRegister += resourceLocation
}
