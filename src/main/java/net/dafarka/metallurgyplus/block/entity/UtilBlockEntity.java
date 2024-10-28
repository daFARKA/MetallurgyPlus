package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class UtilBlockEntity {
    private ItemStackHandler itemHandler;

    public UtilBlockEntity(ItemStackHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    /**
     * Gets the first empty slot.
     *
     * Goes through all slots and returns the first empty slot.
     *
     * @return the i-th slot which is empty or -1 if no slot is empty.
     *
     * */
    public int getFirstEmptySlot() {
        for (int i = 1; i < this.itemHandler.getSlots(); i++) {
            if (this.itemHandler.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the first availabe slot for that (output) item and the respecting amount.
     *
     * @param item the (output) item
     * @param amount the amount the item is producing
     *
     * @return the i-th slot which has that item and the amount can fit or the return value of getFirstEmptySlot(),
     *          -1 if the item cannot be outputted at all.
     *
     * */
    public int getFirstAvailableSlot(Item item, int amount) {
        for (int i = 1; i < this.itemHandler.getSlots(); i++) {
            if (this.itemHandler.getStackInSlot(i).is(item) && (this.itemHandler.getStackInSlot(i).getCount() + amount <= this.itemHandler.getStackInSlot(i).getMaxStackSize())) {
                return i;
            }
        }
        return getFirstEmptySlot();
    }

    /**
     * Gets an item from ForgeRegistries using the name.
     *
     * The respecting item is given by a priority system. First ModItems are returned,
     * if no ModItem exists with that name return a vanilla minecraft item with that name
     * if no vanilla item exists with that name return air. As it is very harmless.
     *
     * @param name the name of the item to return
     *
     * @return an Item with the given name, air otherwise.
     *
     * */
    public Item getItem(String name) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MetallurgyPlus.MODID, name));
        if (item == ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "air"))) {
            item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", name));
        }
        return item;
    }
}
