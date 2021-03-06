package hohserg.advancedauromancy.client.render.wand

import java.util
import java.util.Collections

import javax.vecmath.Matrix4f
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model._
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import org.apache.commons.lang3.tuple.Pair

class WandModel(baseModel: IBakedModel) extends IBakedModel {

  override def getParticleTexture: TextureAtlasSprite = baseModel.getParticleTexture

  override def getQuads(state: IBlockState, side: EnumFacing, rand: Long): util.List[BakedQuad] = Collections.emptyList()

  override def isAmbientOcclusion: Boolean = baseModel.isAmbientOcclusion

  override def isGui3d: Boolean = true

  override def isBuiltInRenderer = false

  override def getItemCameraTransforms: ItemCameraTransforms = baseModel.getItemCameraTransforms

  override def getOverrides: ItemOverrideList = WandItemOverrideList

  override def handlePerspective(cameraTransformType: ItemCameraTransforms.TransformType): Pair[_ <: IBakedModel, Matrix4f] = { //    if (baseChessboardModel instanceof IPerspectiveAwareModel) {
    val matrix4f = baseModel.handlePerspective(cameraTransformType).getRight
    Pair.of(this, matrix4f)
  }

  object WandItemOverrideList extends ItemOverrideList(Collections.emptyList()) {
    val model = new WandFinalisedModel(baseModel)

    override def handleItemState(originalModel: IBakedModel, stack: ItemStack, world: World, entity: EntityLivingBase): IBakedModel = {
      model.itemStack = stack
      model
    }
  }

}
