package hohserg.advancedauromancy.inventory

import hohserg.advancedauromancy.core.Main
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.util.ResourceLocation

class GuiWandBuilder(containerWandBuilder: ContainerWandBuilder) extends GuiContainer(containerWandBuilder){
  override def initGui(): Unit = {
    xSize = 190
    ySize = 234
    super.initGui()
  }

  val background: ResourceLocation = new ResourceLocation(Main.advancedAuromancyModId,"textures/gui/wand_builder.png")

  override def drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int): Unit = {
    mc.getTextureManager.bindTexture(background)
    this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize)
  }

  override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    super.drawScreen(mouseX, mouseY, partialTicks)
    renderHoveredToolTip(mouseX, mouseY)
  }
}
