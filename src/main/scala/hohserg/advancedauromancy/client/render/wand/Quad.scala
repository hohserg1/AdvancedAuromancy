package hohserg.advancedauromancy.client.render.wand

import net.minecraft.client.renderer.texture.TextureAtlasSprite

import scala.language.implicitConversions
import scala.math.Numeric

case class Quad(v1: Vertex, v2: Vertex, v3: Vertex, v4: Vertex, atlas: TextureAtlasSprite) {

}

case class Vertex(x: Float, y: Float, z: Float) {

}

object Vertex {
  @inline implicit def tuple2Vec[F: Numeric](t: (F, F, F)): Vertex = {
    import Numeric.Implicits._
    Vertex(t._1.toFloat, t._2.toFloat, t._3.toFloat)
  }

}
