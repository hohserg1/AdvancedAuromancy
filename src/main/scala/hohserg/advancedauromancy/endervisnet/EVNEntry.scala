package hohserg.advancedauromancy.endervisnet

case class EVNEntry(amount: Float, maxAmount: Int) extends Serializable

object EVNEntry {

  val empty = EVNEntry(0, 0)

}
