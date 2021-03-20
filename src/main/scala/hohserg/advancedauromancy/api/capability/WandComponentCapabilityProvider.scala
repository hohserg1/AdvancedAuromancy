package hohserg.advancedauromancy.api.capability

import hohserg.advancedauromancy.api.capability.WandComponentCapabilityProvider._
import hohserg.advancedauromancy.core.Main
import hohserg.advancedauromancy.items.ItemWandComponent
import hohserg.advancedauromancy.wands.AAResonators.VisResonator
import hohserg.advancedauromancy.wands.WandComponentRegistryEntry
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing.UP
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraftforge.common.capabilities.{Capability, CapabilityInject, ICapabilityProvider}
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import thaumcraft.api.items.ItemsTC

object WandComponentCapabilityProvider {

  def getWandComponent[A <: WandComponentRegistryEntry[A]](stack: ItemStack): A = {
    val capability = stack.getCapability(capa, EnumFacing.UP)
    if (capability != null)
      capability.component
    else
      null
  }.asInstanceOf[A]


  @CapabilityInject(classOf[WandComponentCapability])
  final val capa: Capability[WandComponentCapability] = null

  val name = new ResourceLocation(Main.advancedAuromancyModId, classOf[WandComponentCapability].getSimpleName.toLowerCase)

  @EventBusSubscriber(modid = Main.advancedAuromancyModId)
  object EventHandler {
    @SubscribeEvent
    def attachCapa(e: AttachCapabilitiesEvent[ItemStack]): Unit = {
      e.getObject.getItem match {
        case item: ItemWandComponent[_] =>
          e.addCapability(name, new WandComponentCapabilityProvider(new WandComponentCapability(item.component)))

        case item if item == ItemsTC.visResonator =>
          e.addCapability(name, new WandComponentCapabilityProvider(new WandComponentCapability(VisResonator)))

        case _ =>
      }

    }
  }

}

class WandComponentCapabilityProvider(instance: WandComponentCapability) extends ICapabilityProvider {

  override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = capability == capa && facing == UP

  override def getCapability[T](capability: Capability[T], facing: EnumFacing): T =
    capa.cast(
      if (hasCapability(capability, facing))
        instance
      else
        null
    )
}
