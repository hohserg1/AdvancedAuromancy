package hohserg.advancedauromancy.inventory

import hohserg.advancedauromancy.blocks.BlockWandBuilder.TileWandBuilder
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{Container, Slot}

class ContainerWandBuilder(player: EntityPlayer,tile:TileWandBuilder) extends Container{

  addSlotToContainer(new Slot(tile.inv, 0, 40 + 1 * 24, 40 + 1 * 24))
  addSlotToContainer(new Slot(tile.inv, 2, 40 + 2 * 24, 40 + 0 * 24))
  addSlotToContainer(new Slot(tile.inv, 1, 40 + 0 * 24, 40 + 2 * 24))

  addSlotToContainer(new Slot(tile.inv, 3, 160, 64))

  addSlotToContainer(new Slot(tile.inv, 4, 40 + 0 * 24, 40 + 1 * 24))
  addSlotToContainer(new Slot(tile.inv, 5, 40 + 1 * 24, 40 + 0 * 24))
  addSlotToContainer(new Slot(tile.inv, 6, 40 + 1 * 24, 40 + 2 * 24))
  addSlotToContainer(new Slot(tile.inv, 7, 40 + 2 * 24, 40 + 1 * 24))

  addSlotToContainer(new Slot(tile.inv, 8, 40 + 3 * 24, 40 + -1 * 24))
  addSlotToContainer(new Slot(tile.inv, 9, 40 + 3 * 24, 40 + 0 * 24))
  addSlotToContainer(new Slot(tile.inv, 10, 40 + 2 * 24, 40 + -1 * 24))

  addSlotToContainer(new Slot(tile.inv, 11, 40 + 0 * 24, 40 + 3 * 24))
  addSlotToContainer(new Slot(tile.inv, 12, 40 + -1 * 24, 40 + 3 * 24))
  addSlotToContainer(new Slot(tile.inv, 13, 40 + -1 * 24, 40 + 2 * 24))

  
  for (i <-0 to 2)
    for (j <-0 to 8)
      addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 16 + j * 18, 151 + i * 18))

  for (i <-0 to 8)
    addSlotToContainer(new Slot(player.inventory, i, 16 + i * 18, 209))
  
  override def canInteractWith(playerIn: EntityPlayer): Boolean = true
}
