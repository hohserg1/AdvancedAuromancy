package hohserg.advancedauromancy.client

import java.io.{BufferedReader, InputStreamReader}
import java.util.stream.Collectors

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.{GL11, GL20}

object ShaderEventHandler extends GuiScreen{
  mc = Minecraft.getMinecraft

  lazy val shaderCode: String = {
    val stream = getClass.getResourceAsStream("/assets/advancedauromancy/shaders/test.frag")
    new BufferedReader(new InputStreamReader(stream))
      .lines().collect(Collectors.joining("\n"))
  }

  lazy val shaderID: Int = {
    val id = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER)
    GL20.glShaderSource(id, shaderCode)
    GL20.glCompileShader(id)

    if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
      println("Shader compilation error!\n" +
        GL20.glGetShaderInfoLog(id, GL20.
          glGetShaderi(id, GL20.GL_INFO_LOG_LENGTH)))
      GL20.glDeleteShader(id)
      0
    } else
      id
  }

  lazy val programID: Int = {
    val id = GL20.glCreateProgram()
    GL20.glAttachShader(id, shaderID)
    GL20.glLinkProgram(id)

    if (GL20.glGetProgrami(id, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
      println("Shader link error!\n" +
        GL20.glGetProgramInfoLog(id, GL20.
          glGetProgrami(id, GL20.GL_INFO_LOG_LENGTH)))
      0
    } else {
      GL20.glValidateProgram(id)

      if (GL20.glGetProgrami(id, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
        println("Shader validate error!\n" +
          GL20.glGetProgramInfoLog(id, GL20.
            glGetProgrami(id, GL20.GL_INFO_LOG_LENGTH)))
        0
      } else
        id
    }
  }

  //вот это пашет на других шейдерх, чет не подумал этот потестиь также

  @SubscribeEvent
  def testShaders(event: RenderGameOverlayEvent.Post): Unit = {
    GlStateManager.pushMatrix()
    GlStateManager.pushAttrib()

    GlStateManager.enableAlpha()
    GlStateManager.enableBlend()

    GL20.glUseProgram(programID)
    if(programID!=0) {
      val uniformID = GL20.glGetUniformLocation(programID, "iTime")
      GL20.glUniform1f(uniformID, System.currentTimeMillis()%Float.MaxValue)
    }
    drawTexturedModalRect(0, 0, 0, 0, 256, 256)
    GL20.glUseProgram(0)

    GlStateManager.popAttrib()
    GlStateManager.popMatrix()
  }

}
