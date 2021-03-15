package hohserg.advancedauromancy.client

import java.awt.Color
import java.text.DecimalFormat

import hohserg.advancedauromancy.client.render.simpleItem.{SimpleTexturedModelProvider, TexturedModel}
import hohserg.advancedauromancy.client.render.wand.WandModel
import hohserg.advancedauromancy.core.Main
import hohserg.advancedauromancy.endervisnet.EnderVisNet
import hohserg.advancedauromancy.items.base.Wand
import hohserg.advancedauromancy.items.{ItemEnderWandCasting, ItemWandCasting}
import net.minecraft.client.Minecraft
import net.minecraft.client.gui._
import net.minecraft.client.renderer.block.model.{IBakedModel, ModelResourceLocation}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.translation.I18n
import net.minecraftforge.client.event.{GuiOpenEvent, ModelBakeEvent, TextureStitchEvent}
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.fml.client.{FMLClientHandler, GuiConfirmation}
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase
import net.minecraftforge.fml.relauncher.ReflectionHelper
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11._
import thaumcraft.api.aspects.Aspect
import thaumcraft.client.lib.UtilsFX

import scala.collection.mutable.ListBuffer

class ClientEventHandler extends GuiScreen {
  mc = Minecraft.getMinecraft

  @SubscribeEvent(priority = EventPriority.LOWEST)
  def onTooltip(e: ItemTooltipEvent): Unit = {
    import collection.JavaConverters._
    val stack = e.getItemStack
    stack.getItem match {
      case wand: Wand =>
        val visChargeLabel = I18n.translateToLocal("tc.charge")
        if (stack.getItem == ItemEnderWandCasting)
          e.getToolTip add TextFormatting.AQUA + "Ender vis net owner is " + EnderVisNet.getName(stack).getOrElse("")
        e.getToolTip.set(0, I18n.translateToLocal(wand.getCap(stack).name) + " " + I18n.translateToLocal(wand.getRod(stack).name))
        e.getToolTip.set(e.getToolTip.asScala.indexWhere((param: String) => param.contains(visChargeLabel)), TextFormatting.YELLOW + visChargeLabel + " " + getVisForShow(stack, wand))
        e.getToolTip.add(TextFormatting.WHITE + "Average crafting vis cost is " + wand.getConsumptionModifier(stack, e.getEntityPlayer, crafting = true))
        e.getToolTip.add(TextFormatting.WHITE + "Average casting vis cost is " + wand.getConsumptionModifier(stack, e.getEntityPlayer, crafting = false))
        val upgrades = wand.getCapUpgrades(stack)++wand.getRodUpgrades(stack)
        if (GuiScreen.isShiftKeyDown) {
          e.getToolTip.add(TextFormatting.GREEN + "Upgrades:")
          upgrades.view.map(u => TextFormatting.GREEN + "          " + I18n.translateToLocal(u.name)).foreach(e.getToolTip.add)
        } else
          e.getToolTip.add(TextFormatting.GREEN + "Upgrades: press Shift")
      case _ =>
    }

  }

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
    Wand.wand(wandstack) {
      wand =>

        glPushMatrix()

        val sr = new ScaledResolution(Minecraft.getMinecraft)
        glClear(256)
        glMatrixMode(5889)
        glLoadIdentity()
        glOrtho(0, sr.getScaledWidth_double, sr.getScaledHeight_double, 0, 1000, 3000)
        glMatrixMode(5888)
        glEnable(3042)
        glBlendFunc(770, 771)

        mc.renderEngine.bindTexture(hudTexture)
        val max = wand.getMaxVis(wandstack)
        val cur = wand.getVis(wandstack)

        glTranslatef(42, 6, 0)
        glScaled(0.5D, 0.5D, 0.5D)
        glColor4f(1, 1, 1, 1)

        val loc = (30 * cur / max).toInt

        val ac = new Color(Aspect.AURA.getColor)
        glColor4f(ac.getRed / 255f, ac.getGreen / 255f, ac.getBlue / 255f, 0.8F)
        UtilsFX.drawTexturedQuad(-4, 35 - loc, 104, 0, 8, loc, -90)
        glColor4f(1, 1, 1, 1)

        UtilsFX.drawTexturedQuad(-8, -3, 72, 0, 16, 42, -90)

        glColor4f(1, 1, 1, 1)

        glPopMatrix()

        GL11.glDisable(3042)

        if (mc.player.isSneaking) {
          GL11.glPushMatrix()
          GL11.glScaled(0.5, 0.5, 0.5)
          GL11.glRotatef(-90, 0, 0, 1)
          mc.ingameGUI.drawString(mc.fontRenderer, getVisForShow(wandstack, wand), -44, 80, 16777215)
          GL11.glPopMatrix()
        }
    }

  }

  private def getVisForShow(wandstack: ItemStack, wand: Wand) = {
    visCountFormat format wand.getVis(wandstack)
  }

  private var alreadyEnteredInWorldAutomaticaly = false
  private var mainMenu:GuiMainMenu = null

  @SubscribeEvent
  def onMainMenu(event: GuiOpenEvent): Unit = if (!alreadyEnteredInWorldAutomaticaly) {
    val mc = Minecraft.getMinecraft
    event.getGui match {
      case menu: GuiMainMenu =>
        mainMenu = menu
        mc.displayGuiScreen(new GuiWorldSelection(menu))
      case selection: GuiWorldSelection =>
        val guiListWorldSelection = new GuiListWorldSelection(selection, mc, 100, 100, 32, 100 - 64, 36)
        try
          guiListWorldSelection.getListEntry(0).joinWorld()
        catch {
          case ignore: Exception =>

        }
      case _: GuiConfirmation =>
        alreadyEnteredInWorldAutomaticaly = true
        ReflectionHelper.findMethod(classOf[GuiConfirmation], "actionPerformed", null, classOf[GuiButton]).invoke(event.getGui, new GuiButton(0, 0, 0, ""))
        FMLClientHandler.instance.showGuiScreen(mainMenu)
      case _: GuiIngameMenu => alreadyEnteredInWorldAutomaticaly = true
      case _ =>
    }
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
