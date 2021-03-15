package hohserg.advancedauromancy.utils

import thaumcraft.common.lib.network.PacketHandler

import scala.reflect.ClassTag

object ReflectionUtils {
  def getPrivateField[CL: ClassTag, A](value: CL, field: String): A = {
    val f = implicitly[ClassTag[CL]].runtimeClass.getDeclaredField(field)
    f.setAccessible(true)
    f.get(PacketHandler.INSTANCE).asInstanceOf[A]
  }

}
