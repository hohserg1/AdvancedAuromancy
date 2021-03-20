package hohserg.advancedauromancy.client.render.wand


import java.util

import hohserg.advancedauromancy.api.{WandCap, WandRod}
import hohserg.advancedauromancy.client.render.{BaseFinalisedModel, Cube}
import hohserg.advancedauromancy.items.base.Wand
import hohserg.advancedauromancy.wands._
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.{BakedQuad, IBakedModel}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

import scala.collection.JavaConverters._
import scala.collection.mutable

class WandFinalisedModel(val parentModel: IBakedModel) extends BaseFinalisedModel {
  def isDefined: Boolean = !itemStack.isEmpty

  var itemStack: ItemStack = ItemStack.EMPTY

  def wand: Wand = itemStack.getItem.asInstanceOf[Wand]

  def rod: WandRod = wand.getRod(itemStack)

  def cap: WandCap = wand.getCap(itemStack)

  def focusColor: Option[Int] = wand.getFocusOption(itemStack).flatMap(focus => wand.getFocusStackOption(itemStack).map(focus.getFocusColor))

  def upgrades: List[WandComponentRegistryEntry[_]] = wand.getRodUpgrades(itemStack) ++ wand.getCapUpgrades(itemStack)

  override def getQuads(state: IBlockState, side: EnumFacing, rand: Long): util.List[BakedQuad] = {
    if (side == null && isDefined) {
      val focusTexture = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite("minecraft:blocks/quartz_block_top") //todo: focuses have different type, as simple, advanced, great, and would can have different textures
      val textureRod = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(rod.location.toString)
      val textureCap = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(cap.location.toString)

      val quads = WandFinalisedModel.cap(textureCap) ++
        WandFinalisedModel.rod(textureRod) ++
        focusColor.map(focusColor => Cube(-3, -6, -3, 6, 6, 6, focusTexture, focusColor).scale(0.1f).move(0, 42, 0).toQuads).getOrElse(Nil)

      quads.asJava
    } else
      parentModel.getQuads(state, side, rand)
  }
}

object WandFinalisedModel {

  val fl = 0.2f

  private def cap1Builder(texture: TextureAtlasSprite) = Cube(-1, -1, -1, 2, 2, 2, texture).scale(1.2f, 1, 1.2f).scale(fl).toQuads

  private def cap2Builder(texture: TextureAtlasSprite) = Cube(-1, -1, -1, 2, 2, 2, texture).scale(1.2f, 1, 1.2f).scale(fl).move(0, 20, 0).toQuads

  def rodBuilder(texture: TextureAtlasSprite) = Cube(-1, -1, -1, 2, 18, 2, texture).scale(fl).move(0, 2, 0).toQuads


  type FocusType = Unit //todo: focuses have different type, as simple, advanced, great, and would can have different textures

  val memoizationFocus = new mutable.OpenHashMap[(Int, FocusType), List[BakedQuad]]()
  val memoizationRod = new mutable.OpenHashMap[TextureAtlasSprite, List[BakedQuad]]()
  val memoizationCap = new mutable.OpenHashMap[TextureAtlasSprite, List[BakedQuad]]()

  def rod(implicit texture: TextureAtlasSprite): List[BakedQuad] =
    memoizationRod.getOrElseUpdate(texture, rodBuilder(texture))

  def cap(implicit texture: TextureAtlasSprite): List[BakedQuad] =
    memoizationCap.getOrElseUpdate(texture, cap1Builder(texture) ++ cap2Builder(texture))
}
