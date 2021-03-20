package hohserg.advancedauromancy.wands

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

object AllComponents {

  def getAllComponentsFrom[A: TypeTag : ClassTag, B <: WandComponentRegistryEntry[_] : TypeTag](v: A): Seq[B] = {

    import scala.reflect.runtime.{universe => u}

    val m = u.runtimeMirror(getClass.getClassLoader)

    val instanceMirror = m.reflect(v)

    val t = u.typeOf[A]

    val base = u.typeOf[B]

    val innerObjects = t.members
      .filter(_.isModule)
      .map(_.asModule)
      .filter(_.typeSignature <:< base)
      .map(m.reflectModule)
      .map(_.instance)
      .map(_.asInstanceOf[B])

    val fields = t.members
      .filter(_.isMethod)
      .map(_.asMethod)
      .filter(_.isPublic)
      .filter(_.isGetter)
      .filter(_.returnType <:< base)
      .map(instanceMirror.reflectMethod)
      .map(_.apply())
      .map(_.asInstanceOf[B])

    (innerObjects ++ fields).toSeq
  }

}
