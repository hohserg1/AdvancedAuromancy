package hohserg.advancedauromancy.utils

import net.minecraft.item.{Item, ItemStack}

object ItemUtils {
  def getItemKey(item: Item): String = item.delegate.name().toString

  def equalsStacks(_1: ItemStack, _2: ItemStack): Boolean = Option(_1).flatMap(i=>Option(_2).map(j=>i.getItem==j.getItem && i.getItemDamage==j.getItemDamage)).getOrElse(false)

  def getItemStackKey(itemStack: ItemStack): String = itemStack.getItem.delegate.name().toString


}
