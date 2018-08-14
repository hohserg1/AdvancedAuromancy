package hohserg.advancedauromancy.client.render

import java.util

import javax.vecmath.Matrix4f
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.{BakedQuad, IBakedModel, ItemCameraTransforms, ItemOverrideList}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
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

object IBakedSource {
  val empty: IBakedSource =new IBakedSource {
    override def getQuads(state: IBlockState, side: EnumFacing, rand: Long): util.List[BakedQuad] = util.Collections.emptyList()
  }

}
