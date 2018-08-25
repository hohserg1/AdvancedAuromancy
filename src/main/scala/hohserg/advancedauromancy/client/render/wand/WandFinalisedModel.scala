package hohserg.advancedauromancy.client.render.wand

import java.util

import hohserg.advancedauromancy.client.render.{BaseFinalisedModel, Cube}
import hohserg.advancedauromancy.wands._
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.{BakedQuad, IBakedModel}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad

import scala.collection.mutable

class WandFinalisedModel(val parentModel: IBakedModel, key: (WandRod, WandCap, Option[Int], List[WandUpgrade])) extends BaseFinalisedModel {

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
}

object WandFinalisedModel {

  import collection.JavaConverters._

  val fl = 0.2f
  private def cap1Builder(texture:TextureAtlasSprite) = Cube(-1, -1, -1, 2, 2, 2, texture).scale(1.2f, 1, 1.2f).scale(fl).toQuads
  private def cap2Builder(texture:TextureAtlasSprite) = Cube(-1, -1, -1, 2, 2, 2, texture).scale(1.2f, 1, 1.2f).scale(fl).move(0, 20, 0).toQuads
  private def rodBuilder(texture:TextureAtlasSprite) = Cube(-1, -1, -1, 2, 18, 2, texture).scale(fl).move(0, 2, 0).toQuads


  type FocusType = Any //todo: focuses have different type, as simple, advanced, great, and would can have different textures

  val memoizationFocus = new mutable.OpenHashMap[(Int, FocusType), java.util.List[UnpackedBakedQuad]]()
  val memoizationRod = new mutable.OpenHashMap[TextureAtlasSprite, java.util.List[UnpackedBakedQuad]]()
  val memoizationCap = new mutable.OpenHashMap[TextureAtlasSprite, java.util.List[UnpackedBakedQuad]]()

  def rod(implicit texture: TextureAtlasSprite): java.util.List[UnpackedBakedQuad] =
    memoizationRod.getOrElseUpdate(texture, rodBuilder(texture).asJava)

  def cap(implicit texture: TextureAtlasSprite): java.util.List[UnpackedBakedQuad] =
    memoizationCap.getOrElseUpdate(texture, (cap1Builder(texture)++cap2Builder(texture)).asJava)
}
