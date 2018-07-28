package hohserg.advancedauromancy.client.render.wand

import java.util

import hohserg.advancedauromancy.client.render.Cube
import hohserg.advancedauromancy.wands._
import javax.vecmath.Matrix4f
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.{BakedQuad, IBakedModel, ItemCameraTransforms}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad
import org.apache.commons.lang3.tuple.Pair

import scala.collection.mutable
import scala.math.{cos, sin, toRadians}

class WandFinalisedModel(parentModel: IBakedModel, key: (WandRod, WandCap, Option[Int], List[WandUpgrade])) extends IBakedModel {

  override def getQuads(state: IBlockState, side: EnumFacing, rand: Long): util.List[BakedQuad] = {
    if (side != null) parentModel.getQuads(state, side, rand)
    else {
      val focusTexture = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite("minecraft:blocks/quartz_block_top") //todo: focuses have different type, as simple, advanced, great, and would can have different textures
      val textureRod = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(key._1.location.toString)
      val textureCap = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(key._2.location.toString)

      val r = new util.ArrayList(parentModel.getQuads(state, side, rand))
      r.addAll(WandFinalisedModel.cap(textureCap))
      r.addAll(WandFinalisedModel.rod(textureRod))
      import collection.JavaConverters._
      key._3.map(focusColor => Cube(-3, -6, -3, 6, 6, 6, focusTexture, focusColor).scale(0.1f).move(0, 42, 0).toQuads.asJava).foreach(r.addAll)
      r
    }
  }

  override def isAmbientOcclusion: Boolean = parentModel.isAmbientOcclusion

  override def isGui3d: Boolean = parentModel.isGui3d

  override def isBuiltInRenderer: Boolean = parentModel.isBuiltInRenderer

  override def getParticleTexture: TextureAtlasSprite = parentModel.getParticleTexture

  override def getItemCameraTransforms: ItemCameraTransforms = parentModel.getItemCameraTransforms

  def identity: Matrix4f = {
    val m = new Matrix4f()
    m.setIdentity()
    m
  }

  def rotateY(i: Float): Matrix4f = {
    val m = identity
    m.m00 = cos(toRadians(i)).toFloat
    m.m02 = sin(toRadians(i)).toFloat
    m.m20 = -sin(toRadians(i)).toFloat
    m.m22 = cos(toRadians(i)).toFloat
    m
  }

  def rotateZ(i: Double): Matrix4f = {
    val m = identity
    m.m00 = cos(toRadians(i)).toFloat
    m.m01 = -sin(toRadians(i)).toFloat
    m.m10 = sin(toRadians(i)).toFloat
    m.m11 = cos(toRadians(i)).toFloat
    m
  }

  def scale(x: Double, y: Double, z: Double): Matrix4f = {
    val m = identity
    m.m00 = x.toFloat
    m.m11 = y.toFloat
    m.m22 = z.toFloat
    m
  }

  def scale(d: Double): Matrix4f = {
    val m = identity
    m.m00 = d.toFloat
    m.m11 = d.toFloat
    m.m22 = d.toFloat
    m
  }

  def move(x: Double, y: Double, z: Double): Matrix4f = {
    val m = identity
    m.m03 = x.toFloat
    m.m13 = y.toFloat
    m.m23 = z.toFloat
    m
  }

  override def handlePerspective(cameraTransformType: ItemCameraTransforms.TransformType): Pair[_ <: IBakedModel, Matrix4f] = {
    def matrixOrIdentity(o: Option[Matrix4f]) = o.getOrElse(identity)

    val mat = matrixOrIdentity(Option(parentModel.handlePerspective(cameraTransformType).getRight))

    //Moved to json

    //val a = -4 * -0.5*(1-854/Minecraft.getMinecraft.displayWidth)
    /*
    cameraTransformType match {
      case TransformType.THIRD_PERSON_LEFT_HAND =>
        //mat mul scale(0.2)
        //mat mul move(-0.7,-1,0)
        //mat mul rotateY(45)
        //mat mul move(0.5,0,0.5)

      case TransformType.THIRD_PERSON_RIGHT_HAND =>
        //mat mul scale(0.2)
        //mat mul move(0,-1,-0.7)
        //mat mul rotateY(45)
        //mat mul move(0.5,0,0.5)

      case TransformType.FIRST_PERSON_RIGHT_HAND =>

        //mat mul move(-0.5,0,-0.5)
        //mat mul rotateY(-30)
        //mat mul move(0.5,0,0.5)

        //mat mul scale(0.15)
        //mat mul move(1,-0.5,-a)
        //mat mul rotateY(-30)
        //mat mul rotateZ(25)

      case TransformType.FIRST_PERSON_LEFT_HAND =>
        //mat mul rotateZ(-25)

        //mat mul move(0.5,0,0.5)
        //mat mul rotateY(-30)
        //mat mul move(-0.5,0,-0.5)

        //mat mul scale(0.15)
        //mat mul move(-1,-0.5,a+1)
        //mat mul move(-0.7f,-0.5,2f)

      case TransformType.GROUND =>
        //mat mul scale(0.3)
        //mat mul move(0.5,-1f,0.5)

      case TransformType.GUI =>
        //mat mul rotateZ(-135)
        //mat mul rotateY(45)
        //mat mul scale(0.2f)
        //mat mul move(0.5,-1.5f,0.5)
      case _=>
    }*/
    Pair.of(this, mat)
  }

  override def getOverrides = throw new UnsupportedOperationException("The finalised model does not have an override list.")
}

object WandFinalisedModel {

  import collection.JavaConverters._

  private val textureRod = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(DefaultRod.location.toString)
  private val textureCap = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(DefaultCap.location.toString)

  val fl = 0.2f
  private val cap1Builder = Cube(-1, -1, -1, 2, 2, 2, textureCap).scale(1.2f, 1, 1.2f).scale(fl).builder
  private val cap2Builder = Cube(-1, -1, -1, 2, 2, 2, textureCap).scale(1.2f, 1, 1.2f).scale(fl).move(0, 20, 0).builder
  private val rodBuilder = Cube(-1, -1, -1, 2, 18, 2, textureRod).scale(fl).move(0, 2, 0).builder

  private def setTextureAndBuild(i: UnpackedBakedQuad.Builder)(implicit texture: TextureAtlasSprite) = {
    i.setTexture(texture)
    i.build()
  }

  type FocusType = Any //todo: focuses have different type, as simple, advanced, great, and would can have different textures

  val memoizationFocus = new mutable.OpenHashMap[(Int, FocusType), java.util.List[UnpackedBakedQuad]]()
  val memoizationRod = new mutable.OpenHashMap[TextureAtlasSprite, java.util.List[UnpackedBakedQuad]]()
  val memoizationCap = new mutable.OpenHashMap[TextureAtlasSprite, java.util.List[UnpackedBakedQuad]]()

  def rod(implicit texture: TextureAtlasSprite): java.util.List[UnpackedBakedQuad] =
    memoizationRod.getOrElseUpdate(texture, rodBuilder.map(setTextureAndBuild).asJava)

  def cap(implicit texture: TextureAtlasSprite): java.util.List[UnpackedBakedQuad] =
    memoizationCap.getOrElseUpdate(texture, (cap1Builder.map(setTextureAndBuild) ++ cap2Builder.map(setTextureAndBuild)).asJava)
}
