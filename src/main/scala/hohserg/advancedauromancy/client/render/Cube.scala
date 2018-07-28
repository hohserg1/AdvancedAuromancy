package hohserg.advancedauromancy.client.render

import java.awt.Color

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad

import scala.math._
object Cube{
  def apply(x: Float, y: Float, z: Float, w: Float, h: Float, d: Float,textureAtlasSprite: TextureAtlasSprite,color:(Float,Float,Float)=(1,1,1)): Cube =
    new Cube(x, y, z, w, h, d,0,0,0,1,1,1,textureAtlasSprite,color)

  def apply(x: Float, y: Float, z: Float, w: Float, h: Float, d: Float,textureAtlasSprite: TextureAtlasSprite,color:Int): Cube =
    new Cube(x, y, z, w, h, d,0,0,0,1,1,1,textureAtlasSprite,{
      val c=new Color(color).getRGBColorComponents(new Array(3))
      (c(0),c(1),c(2))
    })
}

case class Cube(
                 x:Float,y:Float,z:Float
                ,w:Float,h:Float,d:Float
                ,cx:Float,cy:Float,cz:Float
                ,scaleX:Float,scaleY:Float,scaleZ:Float,
                 textureAtlasSprite: TextureAtlasSprite,color:(Float,Float,Float)) {

  private def putVertex(builder: UnpackedBakedQuad.Builder, normal: Vec3d, x: Double, y: Double, z: Double, u: Float, v: Float, sprite: TextureAtlasSprite,color:(Float,Float,Float)): Unit = {
    import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage._
    for (
      e <-0 until format.getElementCount
    ) {
      format.getElement(e).getUsage match {
        case POSITION =>
          builder.put(e, x.toFloat, y.toFloat, z.toFloat, 1.0f)
          
        case COLOR =>
          builder.put(e, color._1,color._2,color._3, 1.0f)
          
        case UV =>
          if (format.getElement(e).getIndex == 0) {
            val u1 = sprite.getInterpolatedU(u)
            val v1 = sprite.getInterpolatedV(v)
            builder.put(e, u1, v1, 0f, 1f)
            
          }
        case NORMAL =>
          builder.put(e, normal.x.toFloat, normal.y.toFloat, normal.z.toFloat, 0f)
          
        case _ =>
          builder.put(e)
          
      }
    }
  }

  val format: VertexFormat = net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM

  def extendedVectorScale(v1: Vec3d) = new Vec3d(v1.x*scaleX,v1.y*scaleY,v1.z*scaleZ)

  private def createQuad1(v1: Vec3d, v2: Vec3d, v3: Vec3d, v4: Vec3d, sprite: TextureAtlasSprite, color:(Float,Float,Float)=color):UnpackedBakedQuad.Builder = {
    val center = (cx*scaleX, cy*scaleY, cz*scaleZ)
    createQuad(extendedVectorScale(v1).add(center),extendedVectorScale(v2).add(center),extendedVectorScale(v3).add(center),extendedVectorScale(v4).add(center),sprite,color)
  }

  private def createQuad(v1: Vec3d, v2: Vec3d, v3: Vec3d, v4: Vec3d, sprite: TextureAtlasSprite,color:(Float,Float,Float)=color):UnpackedBakedQuad.Builder = {
    val normal:Vec3d = v1.subtract(v2).crossProduct(v3.subtract(v2))
    val builder = new UnpackedBakedQuad.Builder(format)
    builder.setTexture(sprite)
    putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite,color)
    putVertex(builder, normal, v2.x, v2.y, v2.z, 0, 16, sprite,color)
    putVertex(builder, normal, v3.x, v3.y, v3.z, 16, 16, sprite,color)
    putVertex(builder, normal, v4.x, v4.y, v4.z, 16, 0, sprite,color)
    builder
  }

  def scale(sx:Float,sy:Float,sz:Float): Cube = copy(scaleX=scaleX*sx,scaleY=scaleY*sy,scaleZ=scaleZ*sz)

  def scale(s:Float): Cube = copy(scaleX=scaleX*s,scaleY=scaleY*s,scaleZ=scaleZ*s)

  def move(cx1:Float,cy1:Float,cz1:Float): Cube = copy(cx=cx+cx1,cy=cy+cy1,cz=cz+cz1)

  private val texture = textureAtlasSprite//Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(new ResourceLocation(advancedAuromancyModId+":items/wand_rod_silverwood").toString)

  def toQuads: List[BakedQuad] =
    builder.map(_.build())

  def builder:List[UnpackedBakedQuad.Builder] =
    List(
      createQuad1((x, y, z), (x, y+h, z), (x+w, y+h, z), (x+w, y, z),texture),
      createQuad1((x, y, z), (x+w, y, z), (x+w, y, z+d), (x, y, z+d),texture),
      createQuad1((x, y+h, z),(x, y, z), (x, y, z+d), (x, y+h, z+d),texture),
      createQuad1((x+w, y, z+d),(x+w, y+h, z+d), (x, y+h, z+d), (x, y, z+d),texture),
      createQuad1((x+w, y+h, z+d), (x+w, y+h, z), (x, y+h, z), (x, y+h, z+d),texture),
      createQuad1((x+w, y+h, z+d), (x+w, y, z+d), (x+w, y, z),(x+w, y+h, z),texture)
    )

  @inline implicit private def tuple2Vec[F:Numeric](t:(F ,F ,F )): Vec3d = {
    import Numeric.Implicits._
    new Vec3d(t._1.toDouble,t._2.toDouble,t._3.toDouble)
  }
}
