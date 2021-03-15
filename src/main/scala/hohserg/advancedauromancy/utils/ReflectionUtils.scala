package hohserg.advancedauromancy.utils

import scala.reflect.ClassTag

object ReflectionUtils {
  def getPrivateField[CL: ClassTag, A](value: CL, field: String): A = {
    val f = implicitly[ClassTag[CL]].runtimeClass.getDeclaredField(field)
    f.setAccessible(true)
    f.get(value).asInstanceOf[A]
  }

}
