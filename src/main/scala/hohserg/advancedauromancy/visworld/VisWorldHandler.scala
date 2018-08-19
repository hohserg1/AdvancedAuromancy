package hohserg.advancedauromancy.visworld

import java.awt.Color

import baubles.api.BaublesApi
import hohserg.advancedauromancy.Main
import hohserg.advancedauromancy.items.ItemSenseGoggles
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.EnumFacing._
import net.minecraft.util.math.{BlockPos, RayTraceResult}
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11._
import thaumcraft.api.aspects.AspectHelper

import scala.collection.JavaConverters._
import scala.util.Try

object VisWorldHandler {
  class ClientHandler{
    private val mc = Minecraft.getMinecraft

    @SubscribeEvent
    def onRenderWorld(e:RenderWorldLastEvent): Unit ={
      val player=mc.player
      if(Seq(BaublesApi.getBaublesHandler(player).getStackInSlot(4),player.inventory.armorInventory.get(3)).exists(_.getItem==ItemSenseGoggles)){
        val over = mc.objectMouseOver
        over.typeOfHit match {
          case RayTraceResult.Type.BLOCK=>
            val color = getColorOfBlock(mc.world.getBlockState(over.getBlockPos))
            //println(color.getRGB)
            drawSpecialBlockoverlay(
              over.getBlockPos,
              mc.getRenderPartialTicks,
              color,
              mc.world.getWorldTime/100)

          case _=>
        }

      }

    }
    
    val rotations=Map(
      UP->{()=>glRotatef(90.0F, 1, 0, 0)},
      DOWN->{()=>glRotatef(90.0F, -1, 0, 0)},
      NORTH->{()=>glRotatef(90.0F, 0, 0, 1)},
      SOUTH->{()=>glRotatef(180.0F, 0, 1, 0)},
      WEST->{()=>glRotatef(90.0F, 0, 1, 0)},
      EAST->{()=>glRotatef(90.0F, 0, -1, 0)}
    )

    def drawSpecialBlockoverlay(pos:BlockPos, partialTicks: Float, color: Color, alpha: Double): Unit =
      drawSpecialBlockoverlay(pos.getX,pos.getY,pos.getZ,partialTicks,color,alpha)

    def drawSpecialBlockoverlay(x: Double, y: Double, z: Double, partialTicks: Float, color: Color, alpha: Double): Unit = {
      glPushMatrix()
      val c = color.darker()
      val r = c.getRed / 255.0F
      val g = c.getGreen / 255.0F
      val b = c.getBlue / 255.0F
      val player = Minecraft.getMinecraft.player.asInstanceOf[EntityPlayer]
      val iPX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks
      val iPY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks
      val iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks
      glTranslated(-iPX + x + 0.5D, -iPY + y + 0.5D, -iPZ + z + 0.5D)
      //glColor4f(r,g,b,alpha.toFloat)

      Minecraft.getMinecraft.renderEngine.bindTexture(new ResourceLocation(Main.advancedAuromancyModId,"textures/blocks/visoverlay.png"))

      glDisable(GL_LIGHTING)
      glEnable(3042)
      glBlendFunc(770, 1)
      
      for(side <- 0 to 5){
        glPushMatrix()
        val dir = EnumFacing.values()(side)
        //glTranslated(-iPX + x + 0.5D, -iPY + y + 0.5D, -iPZ + z + 0.5D)
        val decalOffset = 0.501
        glTranslated(decalOffset*dir.getFrontOffsetX, decalOffset*dir.getFrontOffsetY, decalOffset*dir.getFrontOffsetZ)
        val decalScale=1.01
        glScaled(decalScale,decalScale,decalScale)
        rotations(dir).apply()
        renderQuadCentered(
          new ResourceLocation(Main.advancedAuromancyModId,
            "textures/blocks/visoverlay.png")
          ,r,g,b,alpha.toFloat)
        glPopMatrix()
      }
      glPopMatrix()
    }

    def renderQuadCentered(location: ResourceLocation, r: Float, g: Float, b: Float, alpha: Float): Unit = {

      val tessellator: Tessellator = Tessellator.getInstance
      val vertexbuffer = tessellator.getBuffer

      val xCoord = 0
      val yCoord = 0

      vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
      vertexbuffer.pos(xCoord + -0.5, yCoord + 0.5, 0).tex(0, 1).color(r,g,b,alpha).endVertex()
      vertexbuffer.pos(xCoord + 0.5, yCoord + 0.5, 0).tex(1, 1).color(r,g,b,alpha).endVertex()
      vertexbuffer.pos(xCoord + 0.5, yCoord + -0.5, 0).tex(1, 0).color(r,g,b,alpha).endVertex()
      vertexbuffer.pos(xCoord + -0.5, yCoord + -0.5, 0).tex(0, 0).color(r,g,b,alpha).endVertex()

      tessellator.draw()
    }
  }
  def getColorOfBlock(block:IBlockState):Color={
    Try(new ItemStack(Item.getItemFromBlock(block.getBlock))).map(AspectHelper.getObjectAspects)
      .map(asl=>{
        val colorMap=asl.aspects.asScala
          .map{case (as,amount)=>
            (new Color(as.getColor),amount.toFloat)
          }
        val maxAmount=colorMap.max(Ordering.by[(Color,Float),Float](i=>i._2))._2*2
        val colorList=colorMap.map{case (as,amount)=>
          new Color(as.getRed.toFloat/256,as.getGreen.toFloat/256,as.getBlue.toFloat/256,amount/maxAmount)
        }

        colorList.fold(new Color(0xffffff)){case (c1,c2)=>
          val a1=c1.getAlpha
          val r1=c1.getRed
          val g1=c1.getGreen
          val b1=c1.getBlue

          val a2=c2.getAlpha
          val r2=c2.getRed
          val g2=c2.getGreen
          val b2=c2.getBlue
          
          val b = a1 * (255 - a2) / 255
          val a = a2 + b

          if(a > 0) {
            new Color((r2 * a2 + (r1 * b)) / a,(g2 * a2 + (g1 * b)) / a,(b2 * a2 + (b1 * b)) / a,a)
          }else {
            new Color(0)
          }
        }
      }).toOption.getOrElse(new Color(0,0,0,0))
  }

}
