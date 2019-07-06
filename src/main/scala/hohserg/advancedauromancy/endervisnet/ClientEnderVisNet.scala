package hohserg.advancedauromancy.endervisnet

class ClientEnderVisNet extends EnderVisNet {

  override def updateClient(name: String): Unit = ()

  override def loadOrCreate(name: String): EVNEntry = EVNEntry.empty

}
