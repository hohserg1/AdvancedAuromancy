package hohserg.advancedauromancy.client

import net.minecraft.util.ResourceLocation

trait TextureRegister {
  def location: ResourceLocation

  def registerTexture(): Unit =
    ClientEventHandler.registerTexture(location)
}
