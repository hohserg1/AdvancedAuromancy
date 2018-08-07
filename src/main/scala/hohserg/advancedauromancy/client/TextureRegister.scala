package hohserg.advancedauromancy.client

import net.minecraft.util.ResourceLocation

trait TextureRegister {
  def location: ResourceLocation

  ClientEventHandler.registerTexture(location)
}
