package hohserg.advancedauromancy.client.render

import hohserg.advancedauromancy.blocks.BlockWandBuilder
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType
import net.minecraft.client.renderer.entity.RenderEntityItem
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.item.ItemBlock
import org.lwjgl.opengl.GL11._

class TileWandBuilderSpecialRenderer extends TileEntitySpecialRenderer[BlockWandBuilder.TileWandBuilder]{
  lazy val renderItem=new RenderEntityItem(mc.getRenderManager,mc.getRenderItem)
  lazy val mc = Minecraft.getMinecraft

  override def render(te: BlockWandBuilder.TileWandBuilder, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float): Unit =

    if(te.isInstanceOf[BlockWandBuilder.TileWandBuilder]) {
      super.render(te, x, y, z, partialTicks, destroyStage, alpha)
      glPushMatrix()
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0F, 240F)

      glTranslated(x + 0.0625*2, y + 0.9F, z + 0.0625*2)

      val craftMatrix = BlockWandBuilder.craftMatrix

      val wandStack=te.inv.getStackInSlot(3).copy()

      if(!wandStack.isEmpty){
        glTranslated(0.255,-0.17,0.5)
        wandStack.setCount(1)
        val ir = Minecraft.getMinecraft.getItemRenderer
        glRotated(-45,0,1,0)
        glRotated(-90,1,0,0)
        glTranslated(0,-0.0625*3,0.3)
        ir.renderItem(mc.player,wandStack,TransformType.GROUND)

      }else
        for {
          xi <- 0 to 4
          yi <- 0 to 4
          slot = craftMatrix(xi)(yi)
          if slot.isDefined
          (_, slotId) <- slot
          item = te.inv.getStackInSlot(slotId).copy()
          if !item.isEmpty
        } {
          glPushMatrix()
          glTranslated(xi*0.0625*3,0,yi*0.0625*3)
          glScaled(0.5, 0.5, 0.5)
          item.setCount(1)
          val ir = Minecraft.getMinecraft.getItemRenderer
          glRotated(-90,1,0,0)
          glTranslated(0,-0.0625*3,0.3)
          if(!item.getItem.isInstanceOf[ItemBlock]){
            glTranslated(0,0.0625,0)

          }
          ir.renderItem(mc.player,item,TransformType.GROUND)
          glPopMatrix()
        }
      glPopMatrix()
    }

}
