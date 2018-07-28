package hohserg.advancedauromancy.client.render

import javax.vecmath.Matrix4f
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.{IBakedModel, ItemCameraTransforms, ItemOverrideList}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import org.apache.commons.lang3.tuple.Pair

trait IBakedSource extends IBakedModel{
  override lazy val getParticleTexture: TextureAtlasSprite = Minecraft.getMinecraft.getTextureMapBlocks.getMissingSprite

  override val isAmbientOcclusion: Boolean = false

  override def isGui3d: Boolean = true

  override def isBuiltInRenderer = false

  override def getItemCameraTransforms: ItemCameraTransforms = ItemCameraTransforms.DEFAULT

  override def getOverrides: ItemOverrideList = ItemOverrideList.NONE

  override def handlePerspective(cameraTransformType: ItemCameraTransforms.TransformType): Pair[_ <: IBakedModel, Matrix4f] =
    Pair.of(this, new Matrix4f())


}
