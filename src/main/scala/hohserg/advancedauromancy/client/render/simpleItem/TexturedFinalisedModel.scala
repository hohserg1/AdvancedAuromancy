package hohserg.advancedauromancy.client.render.simpleItem

import java.util
import java.util.Collections

import hohserg.advancedauromancy.client.render.BaseFinalisedModel
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model._
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.EnumFacing

class TexturedFinalisedModel(val parentModel: IBakedModel, key:String)  extends BaseFinalisedModel {

  import java.util.Optional

  import com.google.common.collect.ImmutableList
  import net.minecraft.client.renderer.block.model.BakedQuad
  import net.minecraft.client.renderer.texture.TextureAtlasSprite
  import net.minecraftforge.client.model.ItemLayerModel

  val textureAtlasSprite: TextureAtlasSprite = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(key)
  val quads: ImmutableList[BakedQuad] = ItemLayerModel.getQuadsForSprite(0, textureAtlasSprite,DefaultVertexFormats.ITEM,Optional.empty())
  println(key,textureAtlasSprite)

  override def getQuads(state: IBlockState, side: EnumFacing, rand: Long): util.List[BakedQuad] = {
    if (side != null)
      Collections.emptyList()
    else
      quads
  }
}