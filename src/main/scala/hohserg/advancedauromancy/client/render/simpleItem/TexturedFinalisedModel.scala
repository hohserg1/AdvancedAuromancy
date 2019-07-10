package hohserg.advancedauromancy.client.render.simpleItem

import java.util
import java.util.{Collections, Optional}

import com.google.common.collect.ImmutableList
import hohserg.advancedauromancy.client.render.BaseFinalisedModel
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.{BakedQuad, _}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.model.ItemLayerModel

class TexturedFinalisedModel(val parentModel: IBakedModel, val key: String) extends BaseFinalisedModel {

  val textureAtlasSprite: TextureAtlasSprite = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(key)
  val quads: ImmutableList[BakedQuad] = ItemLayerModel.getQuadsForSprite(0, textureAtlasSprite, DefaultVertexFormats.ITEM, Optional.empty())

  override def getQuads(state: IBlockState, side: EnumFacing, rand: Long): util.List[BakedQuad] = {
    if (side != null)
      Collections.emptyList()
    else
      quads
  }
}