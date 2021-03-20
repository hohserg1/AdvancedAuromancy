package hohserg.advancedauromancy.wands

import baubles.api.BaublesApi
import hohserg.advancedauromancy.api.RodUpgrade
import hohserg.advancedauromancy.api.WandRod.identityOnUpdate
import hohserg.advancedauromancy.client.render.Cube
import hohserg.advancedauromancy.core.Main.advancedAuromancyModId
import hohserg.advancedauromancy.items.base.Wand
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import thaumcraft.api.items.{IRechargable, RechargeHelper}
import thaumcraft.common.world.aura.AuraHandler

object AARodUpgrades {

  object ChargeIndicator extends RodUpgrade(0, 10, identityOnUpdate,
    stack => quads => {
      val (charge, maxCharge) = Wand.wand[(Float, Int)](stack)({ wand => wand.getVis(stack) -> wand.getMaxVis(stack) }, 0f -> 1)

      val textureAtlasSprite = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(advancedAuromancyModId + ":rods_and_caps/wand_charge_indicator")
      quads ++
        Cube(-1.1f, 1, 0.1f, 1.1f, 14 * (charge / maxCharge.toFloat), 1.1f, textureAtlasSprite, applyDiffuseLighting = false).scale(0.2f).move(0, 2, 0).toQuads
    })

  object CapacityIntercalation extends RodUpgrade(50, 100, identityOnUpdate)

  object VisAbsorption extends RodUpgrade(0, 100, (stack, player) =>
    if (player.world.rand.nextInt(100) == 0)
      Wand.wand(stack) {
        wand => wand.addVis(stack, AuraHandler.drainVis(player.world, player.getPosition, 1, false))
      }
  )

  private def itemsView(size: Int, stackByIndex: Int => ItemStack) =
    (0 until size) map stackByIndex

  object InventoryCharger extends RodUpgrade(0, 100, (stack, player) =>
    if (!player.world.isRemote && player.ticksExisted % 40 == 0)
      Wand.wand(stack) {
        wand =>
          if (wand.getVis(stack) >= 1) {
            val baubles = BaublesApi.getBaublesHandler(player)
            val armor = player.inventory.armorInventory
            val itemsToCharge =
              itemsView(player.inventory.getSizeInventory, player.inventory.getStackInSlot) ++
                itemsView(baubles.getSlots, baubles.getStackInSlot) ++
                itemsView(armor.size(), armor.get)

            val canBeChargered = itemsToCharge
              .map(i => i -> i.getItem)
              .collectFirst { case (itemStack, item: IRechargable) if itemStack != stack && item.getMaxCharge(itemStack, player) - RechargeHelper.getCharge(itemStack) >= 1 =>
                itemStack
              }

            canBeChargered.foreach { i =>
              val chargered = RechargeHelper.rechargeItemBlindly(i, player, 1)
              wand.setVis(stack, wand.getVis(stack) - chargered)
            }
          }
      }
  )

  object DefaultRodUpgrade extends RodUpgrade(0, 0, identityOnUpdate) {
    override def isDefault = true
  }
  lazy val values = AllComponents.getAllComponentsFrom[this.type, RodUpgrade](this)

}
