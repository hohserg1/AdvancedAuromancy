package hohserg.advancedauromancy.client.render

import javax.vecmath.Matrix4f
import net.minecraft.client.renderer.block.model.{IBakedModel, ItemCameraTransforms}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import org.apache.commons.lang3.tuple.Pair

import scala.math.{cos, sin, toRadians}

trait BaseFinalisedModel extends IBakedModel {
  def parentModel: IBakedModel
  override def isAmbientOcclusion: Boolean = parentModel.isAmbientOcclusion

  override def isGui3d: Boolean = parentModel.isGui3d

  override def isBuiltInRenderer: Boolean = parentModel.isBuiltInRenderer

  override def getParticleTexture: TextureAtlasSprite = parentModel.getParticleTexture

  override def getItemCameraTransforms: ItemCameraTransforms = parentModel.getItemCameraTransforms

  def identityMatrix: Matrix4f = {
    val m = new Matrix4f()
    m.setIdentity()
    m
  }

  def rotateY(i: Float): Matrix4f = {
    val m = identityMatrix
    m.m00 = cos(toRadians(i)).toFloat
    m.m02 = sin(toRadians(i)).toFloat
    m.m20 = -sin(toRadians(i)).toFloat
    m.m22 = cos(toRadians(i)).toFloat
    m
  }

  def rotateZ(i: Double): Matrix4f = {
    val m = identityMatrix
    m.m00 = cos(toRadians(i)).toFloat
    m.m01 = -sin(toRadians(i)).toFloat
    m.m10 = sin(toRadians(i)).toFloat
    m.m11 = cos(toRadians(i)).toFloat
    m
  }

  def scale(x: Double, y: Double, z: Double): Matrix4f = {
    val m = identityMatrix
    m.m00 = x.toFloat
    m.m11 = y.toFloat
    m.m22 = z.toFloat
    m
  }

  def scale(d: Double): Matrix4f = {
    val m = identityMatrix
    m.m00 = d.toFloat
    m.m11 = d.toFloat
    m.m22 = d.toFloat
    m
  }

  def move(x: Double, y: Double, z: Double): Matrix4f = {
    val m = identityMatrix
    m.m03 = x.toFloat
    m.m13 = y.toFloat
    m.m23 = z.toFloat
    m
  }

  override def handlePerspective(cameraTransformType: ItemCameraTransforms.TransformType): Pair[_ <: IBakedModel, Matrix4f] = {
    def matrixOrIdentity(o: Option[Matrix4f]) = o.getOrElse(identityMatrix)

    val mat = matrixOrIdentity(Option(parentModel.handlePerspective(cameraTransformType).getRight))
    Pair.of(this, mat)
  }

  override def getOverrides = throw new UnsupportedOperationException("The finalised model does not have an override list.")

}
