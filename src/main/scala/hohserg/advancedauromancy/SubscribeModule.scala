package hohserg.advancedauromancy

import collection.JavaConverters._

import net.minecraftforge.fml.common.discovery.ASMDataTable

import scala.annotation.StaticAnnotation

class SubscribeModule extends StaticAnnotation{

}

object SubscribeModule {
  def extractAsSequence(asm: ASMDataTable) = {
    val annotation:Class[SubscribeModule]=classOf[SubscribeModule]
    asm.getAll(annotation.getName).asScala.toSeq.flatMap(it=>{
      try{
        Seq(Class.forName(it.getClassName).getDeclaredField("MODULE$").get(null))
      }catch {
        case e:Exception=>
          println("Can't loaded module "+it.getClassName)
          e.printStackTrace()
          Seq()
      }
    })
  }

}
