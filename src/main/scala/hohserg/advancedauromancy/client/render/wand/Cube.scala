package hohserg.advancedauromancy.client.render.wand

import net.minecraft.client.renderer.texture.TextureAtlasSprite

object Cube {
  def apply(x: Float, y: Float, z: Float,
            w: Float, h: Float, d: Float,
            atlas: TextureAtlasSprite): ModelEntry =
    ModelEntry.withQuads(
      "bottom" -> Quad((x, y, z), (x + w, y, z), (x + w, y, z + d), (x, y, z + d), atlas),
      "top" -> Quad((x, y + h, z), (x + w, y + h, z), (x + w, y + h, z + d), (x, y + h, z + d), atlas)
    )

}
