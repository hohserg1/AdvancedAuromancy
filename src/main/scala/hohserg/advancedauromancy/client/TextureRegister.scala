package hohserg.advancedauromancy.client

import hohserg.advancedauromancy.client.render.ClientEventHandler
import net.minecraft.util.ResourceLocation

trait TextureRegister {
  def location: ResourceLocation

  ClientEventHandler.registerTexture(location)
}
