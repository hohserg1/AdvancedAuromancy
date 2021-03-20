package hohserg.advancedauromancy.client.render.simpleItem

import java.util
import java.util.Collections

import javax.vecmath.Matrix4f
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.{BakedQuad, IBakedModel, ItemCameraTransforms, ItemOverrideList}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import org.apache.commons.lang3.tuple.Pair

import scala.collection.mutable

class TexturedModel(baseModel: IBakedModel) extends IBakedModel {
  override def getParticleTexture: TextureAtlasSprite = baseModel.getParticleTexture

  override def getQuads(state: IBlockState, side: EnumFacing, rand: Long): util.List[BakedQuad] = baseModel.getQuads(state, side, rand)

  override def isAmbientOcclusion: Boolean = baseModel.isAmbientOcclusion

  override def isGui3d: Boolean = true

  override def isBuiltInRenderer = false

  override def getItemCameraTransforms: ItemCameraTransforms = baseModel.getItemCameraTransforms

  override def getOverrides: ItemOverrideList = TexturedItemOverrideList

  override def handlePerspective(cameraTransformType: ItemCameraTransforms.TransformType): Pair[_ <: IBakedModel, Matrix4f] = { //    if (baseChessboardModel instanceof IPerspectiveAwareModel) {
    val matrix4f = baseModel.handlePerspective(cameraTransformType).getRight
    Pair.of(this, matrix4f)
  }

  object TexturedItemOverrideList extends ItemOverrideList(Collections.emptyList()) {
    type Key = String
    val memoization = new mutable.OpenHashMap[Key, TexturedFinalisedModel]()

    private def model(originalModel: IBakedModel, stack: ItemStack) = {
      stack.getItem match {
        case item: SimpleTexturedModelProvider =>
          val key = item.textureName
          memoization.getOrElseUpdate(key, new TexturedFinalisedModel(originalModel, key))
        case _ => originalModel
      }
    }

    override def handleItemState(originalModel: IBakedModel, stack: ItemStack, world: World, entity: EntityLivingBase): IBakedModel =
      model(originalModel, stack)

  }

}
