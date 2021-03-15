package hohserg.advancedauromancy.utils

object ReflectionUtils {
  def getPrivateField[A](value: Any, field: String): A = {
    val f = value.getClass.getDeclaredField(field)
    f.setAccessible(true)
    f.get(value).asInstanceOf[A]
  }
}
