package hohserg.advancedauromancy.visworld

import java.awt.Color

import net.minecraft.block.state.IBlockState
import net.minecraft.item.{Item, ItemStack}
import thaumcraft.api.aspects.AspectHelper

import scala.collection.JavaConverters._
import scala.util.Try

object VisWorldHandler {
  def getColorOfBlock(block:IBlockState):Color={
    Try(new ItemStack(Item.getItemFromBlock(block.getBlock))).map(AspectHelper.getObjectAspects)
      .map(asl=>{
        val colorMap=asl.aspects.asScala
          .map{case (as,amount)=>
            (new Color(as.getColor),amount.toFloat)
          }
        val maxAmount=colorMap.max(Ordering.by(i=>i._2))._2*2
        val colorList=colorMap.map{case (as,amount)=>
          new Color(as.getRed.toFloat/256,as.getGreen.toFloat/256,as.getBlue.toFloat/256,amount/maxAmount)
        }

        colorList.fold(new Color(0xffffff)){case (c1,c2)=>
          val a1=c1.getAlpha
          val r1=c1.getRed
          val g1=c1.getGreen
          val b1=c1.getBlue

          val a2=c2.getAlpha
          val r2=c2.getRed
          val g2=c2.getGreen
          val b2=c2.getBlue
          
          val b = a1 * (255 - a2) / 255
          val a = a2 + b

          if(a > 0) {
            new Color((r2 * a2 + (r1 * b)) / a,(g2 * a2 + (g1 * b)) / a,(b2 * a2 + (b1 * b)) / a,a)
          }else {
            new Color(0)
          }
        }
      }).toOption.getOrElse(new Color(0,0,0,0))
  }

}
