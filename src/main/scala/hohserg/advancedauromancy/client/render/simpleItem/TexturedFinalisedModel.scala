package hohserg.advancedauromancy.client.render.simpleItem

import java.util

import hohserg.advancedauromancy.client.render.BaseFinalisedModel
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.{BakedQuad, IBakedModel}
import net.minecraft.util.EnumFacing

class TexturedFinalisedModel(val parentModel: IBakedModel, key:String)  extends BaseFinalisedModel {

  override def getQuads(state: IBlockState, side: EnumFacing, rand: Long): util.List[BakedQuad] = {
    if (side != null) parentModel.getQuads(state, side, rand)
    else {
      parentModel.getQuads(state, side, rand)

    }
  }
}
