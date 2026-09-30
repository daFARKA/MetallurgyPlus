package net.dafarka.metallurgyplus.block.entity;


import net.minecraft.world.item.Item;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;


public class UtilBlockEntity {
    private final CombinedInvWrapper itemHandler;

    public UtilBlockEntity(CombinedInvWrapper itemHandler) {
        this.itemHandler = itemHandler;
    }

    /**
     * Gets the first empty slot.
     * <p>
     * Goes through all slots and returns the first empty slot.
     *
     * @param slot the slot number of the first output slot [including]
     *
     * @return the i-th slot which is empty or -1 if no slot is empty.
     */
    public int getFirstEmptySlot(int slot) {
        for (int i = slot; i < this.itemHandler.getSlots(); i++) {
            if (this.itemHandler.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the first available slot for that (output) item and the respecting amount.
     *
     * @param item   the (output) item
     * @param amount the amount the item is producing
     * @param slot   the slot number of the first output slot [including]
     *
     * @return the i-th slot which has that item and the amount can fit or the return value of getFirstEmptySlot(),
     * -1 if the item cannot be outputted at all.
     */
    public int getFirstAvailableSlot(Item item, int amount, int slot) {
        for (int i = slot; i < this.itemHandler.getSlots(); i++) {
            if (this.itemHandler.getStackInSlot(i).is(item) && (this.itemHandler.getStackInSlot(i).getCount() + amount <= this.itemHandler.getStackInSlot(i).getMaxStackSize())) {
                return i;
            }
        }
        return getFirstEmptySlot(slot);
    }
}
