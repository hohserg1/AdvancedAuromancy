package hohserg.advancedauromancy.inventory

import hohserg.advancedauromancy.blocks.BlockWandBuilder.TileWandBuilder
import hohserg.advancedauromancy.items.base.Wand
import hohserg.advancedauromancy.wands.RodsAndCaps._
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{Container, InventoryBasic, Slot}
import net.minecraft.item.ItemStack

class ContainerWandBuilder(player: EntityPlayer, tile: TileWandBuilder) extends Container {

  addSlotToContainer(new FilteredSlot(rodByStack.andThen(_.isDefined), tile.inv, 0, 40 + 1 * 24, 40 + 1 * 24))
  addSlotToContainer(new FilteredSlot(capByStack.andThen(_.isDefined), tile.inv, 2, 40 + 2 * 24, 40 + 0 * 24))
  addSlotToContainer(new FilteredSlot(capByStack.andThen(_.isDefined), tile.inv, 1, 40 + 0 * 24, 40 + 2 * 24))

  addSlotToContainer(new FilteredSlot(_.getItem.isInstanceOf[Wand], tile.inv, 3, 160, 64))

  addSlotToContainer(new FilteredSlot(upgradeByStack.andThen(_.isDefined), tile.inv, 4, 40 + 0 * 24, 40 + 1 * 24))
  addSlotToContainer(new FilteredSlot(upgradeByStack.andThen(_.isDefined), tile.inv, 5, 40 + 1 * 24, 40 + 0 * 24))
  addSlotToContainer(new FilteredSlot(upgradeByStack.andThen(_.isDefined), tile.inv, 6, 40 + 1 * 24, 40 + 2 * 24))
  addSlotToContainer(new FilteredSlot(upgradeByStack.andThen(_.isDefined), tile.inv, 7, 40 + 2 * 24, 40 + 1 * 24))

  addSlotToContainer(new FilteredSlot(upgradeByStack.andThen(_.isDefined), tile.inv, 8, 40 + 3 * 24, 40 + -1 * 24))
  addSlotToContainer(new FilteredSlot(upgradeByStack.andThen(_.isDefined), tile.inv, 9, 40 + 3 * 24, 40 + 0 * 24))
  addSlotToContainer(new FilteredSlot(upgradeByStack.andThen(_.isDefined), tile.inv, 10, 40 + 2 * 24, 40 + -1 * 24))

  addSlotToContainer(new FilteredSlot(upgradeByStack.andThen(_.isDefined), tile.inv, 11, 40 + 0 * 24, 40 + 3 * 24))
  addSlotToContainer(new FilteredSlot(upgradeByStack.andThen(_.isDefined), tile.inv, 12, 40 + -1 * 24, 40 + 3 * 24))
  addSlotToContainer(new FilteredSlot(upgradeByStack.andThen(_.isDefined), tile.inv, 13, 40 + -1 * 24, 40 + 2 * 24))


  for (i <- 0 to 2)
    for (j <- 0 to 8)
      addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 16 + j * 18, 151 + i * 18))

  for (i <- 0 to 8)
    addSlotToContainer(new Slot(player.inventory, i, 16 + i * 18, 209))

  override def canInteractWith(playerIn: EntityPlayer): Boolean = true

  override def transferStackInSlot(playerIn: EntityPlayer, slotNumber: Int): ItemStack = {
    import net.minecraft.item.ItemStack
    var itemstack: ItemStack = ItemStack.EMPTY
    val slot = inventorySlots.get(slotNumber)
    if (slot != null && slot.getHasStack) {
      val itemstack1: ItemStack = slot.getStack
      itemstack = itemstack1.copy
      if (slotNumber <= 13) {
        if (!this.mergeItemStack(itemstack1, 14, 43, true)) {
          ItemStack.EMPTY
        } else {
          slot.onSlotChange(itemstack1, itemstack)
          ItemStack.EMPTY
        }
      } else if (slotNumber > 13) {
        ItemStack.EMPTY
      } else {
        if (itemstack1.getCount == 0) {
          slot.putStack(ItemStack.EMPTY)
        }
        else {
          slot.onSlotChanged()
          ItemStack.EMPTY
        }
        if (itemstack1.getCount == itemstack.getCount) {
          ItemStack.EMPTY
        } else
          slot.onTake(playerIn, itemstack1)
      }
    } else ItemStack.EMPTY
  }

  class FilteredSlot(predicate: ItemStack => Boolean, inv: InventoryBasic, i: Int, i1: Int, i2: Int) extends Slot(inv, i, i1, i2){
    override def isItemValid(stack: ItemStack): Boolean = predicate(stack)
  }

}
