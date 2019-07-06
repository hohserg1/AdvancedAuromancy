package hohserg.advancedauromancy.core

import hohserg.advancedauromancy.endervisnet.ServerEnderVisNet

class ServerProxy extends CommonProxy {

  override lazy val enderVisNet = new ServerEnderVisNet

}
