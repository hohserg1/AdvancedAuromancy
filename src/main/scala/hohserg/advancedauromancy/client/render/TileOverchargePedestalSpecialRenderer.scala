package hohserg.advancedauromancy.client.render

import hohserg.advancedauromancy.blocks.BlockOverchargePedestal.TileOverchargePedestal
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.entity.item.EntityItem
import org.lwjgl.opengl.GL11

class TileOverchargePedestalSpecialRenderer extends TileEntitySpecialRenderer[TileOverchargePedestal] {
  override def render(te: TileOverchargePedestal, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float): Unit = {
    super.render(te, x, y, z, partialTicks, destroyStage, alpha)
    if (te != null) {
      val itemStack = te.inv.getStackInSlot(0)
      if (!itemStack.isEmpty) {
        GL11.glPushMatrix()

        val mc = Minecraft.getMinecraft
        val entityItem = new EntityItem(mc.world, 0, 0, 0, itemStack)
        entityItem.hoverStart = 0

        GL11.glTranslated(x + 0.5, y + 0.75, z + 0.5)
        GL11.glScaled(1.5D, 1.5D, 1.5D)
        val ticks = mc.getRenderViewEntity.ticksExisted.toFloat + partialTicks
        GL11.glRotatef(ticks % 360, 0, 1, 0)
        mc.getRenderManager.renderEntity(entityItem, 0, 0, 0, 0, 0, false)

        GL11.glPopMatrix()
      }
    }
  }
}
