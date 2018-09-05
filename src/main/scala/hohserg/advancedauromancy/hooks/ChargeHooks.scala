package hohserg.advancedauromancy.hooks

import java.awt.Color

import hohserg.advancedauromancy.hooklib.asm.{Hook, ReturnCondition}
import hohserg.advancedauromancy.items.base.Wand
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagInt
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.lwjgl.opengl.GL11._
import thaumcraft.api.aspects.{Aspect, AspectList}
import thaumcraft.api.aura.AuraHelper
import thaumcraft.api.items.{IRechargable, RechargeHelper}
import thaumcraft.client.lib.UtilsFX
import thaumcraft.client.lib.events.HudHandler
import thaumcraft.common.tiles.devices.TileRechargePedestal

object ChargeHooks {
  /*
  @Hook(at = new At(point = InjectionPoint.RETURN))
  def drawSlot (gui:GuiContainer, slotIn: Slot):Unit = {
    val i = slotIn.xPos
    val j = slotIn.yPos
    Minecraft.getMinecraft.fontRenderer.drawString(slotIn.getSlotIndex.toString,i,j,0xff00ff)
  }*/
  /*
  val mc = Minecraft.getMinecraft
  var drawingBackground=false
  lazy private val particleTexture = new ResourceLocation(Main.advancedAuromancyModId, "textures/particle/particle.png")
  @Hook()
  def genResearchBackgroundZoomable(gui:GuiResearchBrowser, mx: Int, my: Int, par3: Float, locX: Int, locY: Int): Unit ={
    drawingBackground=true
    GL11.glPushMatrix()
    //Minecraft.getMinecraft.renderEngine.bindTexture(particleTexture)
    //gui.drawTexturedModalRectWithDoubles((16 - 2).toFloat * 1, (16 - 2).toFloat * 1, locX.toDouble / 2.0D, locY.toDouble / 2.0D, ((gui.width-32 + 4).toFloat * 1).toDouble, ((gui.height-32 + 4).toFloat * 1).toDouble)
    GL11.glPopMatrix()
  }
  private val shaderCallback = new ShaderCallback()
  {
    def call(i:Int)
    {
      /*
      val x = ARBShaderObjects.glGetUniformLocationARB(i, "yaw")
      ARBShaderObjects.glUniform1fARB(x, (mc.player.rotationYaw * 2.0F * 3.141592653589793D / 360.0D).toFloat)

      val z = ARBShaderObjects.glGetUniformLocationARB(i, "pitch")
      ARBShaderObjects.glUniform1fARB(z, -(mc.player.rotationPitch * 2.0F * 3.141592653589793D / 360.0D).toFloat)*/
    }
  }

  def drawTexturedModalRectWithDoubles1(gui:GuiResearchBrowser,xCoord: Float, yCoord: Float, minU: Double, minV: Double, maxU: Double, maxV: Double): Unit = {
    val f2 = 0.00390625F
    val f3 = 0.00390625F
    val tessellator = Tessellator.getInstance
    val VertexBuffer = tessellator.getBuffer
    VertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX)
    VertexBuffer.pos((xCoord + 0.0F).toDouble, yCoord.toDouble + maxV, 0/*gui.zLevel.toDouble*/).tex((minU + 0.0D) * f2.toDouble, (minV + maxV) * f3.toDouble).endVertex()
    VertexBuffer.pos(xCoord.toDouble + maxU, yCoord.toDouble + maxV, 0/*gui.zLevel.toDouble*/).tex((minU + maxU) * f2.toDouble, (minV + maxV) * f3.toDouble).endVertex()
    VertexBuffer.pos(xCoord.toDouble + maxU, (yCoord + 0.0F).toDouble, 0/*gui.zLevel.toDouble*/).tex((minU + maxU) * f2.toDouble, (minV + 0.0D) * f3.toDouble).endVertex()
    VertexBuffer.pos((xCoord + 0.0F).toDouble, (yCoord + 0.0F).toDouble, 0/*gui.zLevel.toDouble*/).tex((minU + 0.0D) * f2.toDouble, (minV + 0.0D) * f3.toDouble).endVertex()
    tessellator.draw()
  }

  @Hook()
  def drawTexturedModalRectWithDoubles(gui:GuiResearchBrowser, xCoord: Float, yCoord: Float, minU: Double, minV: Double, maxU: Double, maxV: Double): Unit ={
    if(drawingBackground){
      drawingBackground=false
      ShaderHelper.useShader(ShaderHelper.endShader, this.shaderCallback)
      drawTexturedModalRectWithDoubles1(gui,xCoord,yCoord,minU,minV,maxU,maxV)
      ShaderHelper.releaseShader()
    }else drawTexturedModalRectWithDoubles1(gui,xCoord,yCoord,minU,minV,maxU,maxV)

  }*/


  lazy private val hudTexture = new ResourceLocation("thaumcraft", "textures/gui/hud.png")
  @Hook(injectOnExit = true)
  def renderCastingWandHud(hudHandler: HudHandler, mc:Minecraft, partialTicks:Float, player:EntityPlayer, time:Long, wandstack:ItemStack):Unit = {
    wandstack.getItem match{
      case wand:Wand=>

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

        glPopMatrix()
      case _=>

    }

  }
  @Hook(returnCondition = ReturnCondition.ALWAYS)
  def getAspects(tile: TileRechargePedestal): AspectList = {
    val stackInSlot = tile.getStackInSlot(0)
    if (stackInSlot != null && stackInSlot.getItem.isInstanceOf[IRechargable]) {
      val c = stackInSlot.getItem match {
        case wand: Wand => wand.getVis(stackInSlot).toInt
        case _ => RechargeHelper.getCharge(stackInSlot)
      }
      new AspectList().add(Aspect.ENERGY, c)
    }
    else null
  }

  private def setCharge(is: ItemStack, player: EntityLivingBase, amt: Int): Unit = {
    is.setTagInfo("tc.charge", new NBTTagInt(amt))
  }

  @Hook(returnCondition = ReturnCondition.ALWAYS)
  def rechargeItem(rh: RechargeHelper, world: World, is: ItemStack, pos: BlockPos, player: EntityPlayer, amt: Int): Float =
    if (is != null && is.getItem.isInstanceOf[IRechargable]) {
      val chargeItem = is.getItem.asInstanceOf[IRechargable]
      if (player != null && AuraHelper.shouldPreserveAura(world, player, pos))
        0
      else {
        val maxCharge = chargeItem.getMaxCharge(is, player)
        val (currentCharge,multiplier) = is.getItem match {
          case wand: Wand => ((wand.getVis(is)*1000).toInt,1000)
          case _ => (RechargeHelper.getCharge(is),1)
        }
        val amt2 = Math.min(amt, Math.ceil((maxCharge - currentCharge).toFloat / multiplier).toInt)
        val drained = AuraHelper.drainVis(world, pos, amt2.toFloat, false).toInt
        if (drained > 0) {
          val resultCharge = Math.min(maxCharge, drained * multiplier + currentCharge)

          is.getItem match {
            case wand: Wand => wand.setVis(is, resultCharge.toFloat/1000)
            case _ => setCharge(is, player, resultCharge)
          }
          drained.toFloat
        }
        else 0
      }
    }
    else 0

}
