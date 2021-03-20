package hohserg.advancedauromancy.research

sealed trait Status

object Status {
  trait NotConfigured extends Status

  trait Configured extends Status

}
