package hohserg.advancedauromancy.wands

object Test extends App {

  abstract class A {
    override def hashCode(): Int = {
      println("test hashCode")
      super.hashCode()
    }

    override def equals(obj: Any): Boolean = {
      println("test equals")
      super.equals(obj)
    }
  }

  case class B(a: Int, b: String) extends A

  println(B(0, "test").hashCode())
  println(B(0, "test") == B(0, "test"))

}
