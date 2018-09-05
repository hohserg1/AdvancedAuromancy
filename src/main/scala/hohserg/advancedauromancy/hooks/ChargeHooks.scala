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
