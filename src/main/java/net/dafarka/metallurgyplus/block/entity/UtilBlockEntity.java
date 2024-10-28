package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
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
     * @param slot the slot number of the first output slot [including]
     *
     * @return the i-th slot which is empty or -1 if no slot is empty.
     *
     * */
    public int getFirstEmptySlot(int slot) {
        for (int i = slot; i < this.itemHandler.getSlots(); i++) {
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
     * @param slot the slot number of the first output slot [including]
     *
     * @return the i-th slot which has that item and the amount can fit or the return value of getFirstEmptySlot(),
     *          -1 if the item cannot be outputted at all.
     *
     * */
    public int getFirstAvailableSlot(Item item, int amount, int slot) {
        for (int i = slot; i < this.itemHandler.getSlots(); i++) {
            if (this.itemHandler.getStackInSlot(i).is(item) && (this.itemHandler.getStackInSlot(i).getCount() + amount <= this.itemHandler.getStackInSlot(i).getMaxStackSize())) {
                return i;
            }
        }
        return getFirstEmptySlot(slot);
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

    /**
     * Gets the first slot that contains the ingredient.
     *
     * If startSlot is equal to endSlot, there is only one input slot.
     *
     * @param ingredient the ingredients that is in a slot
     * @param startSlot the number of the start slot [including]
     * @param endSlot the number of the end slot [excluding]
     *
     * @return the i-th slot number which contains an input item and the amount suffices. Otherwise -1.
     *
     * */
    public int getFirstSlotThatContainsAnyOfInputItems(Ingredient ingredient, int startSlot, int endSlot) {
        if (startSlot == endSlot) {
            if (ingredient.test(this.itemHandler.getStackInSlot(startSlot))) {
                return startSlot;
            }
            return -1;
        }
        for (int i = startSlot; i < endSlot; i++) {
            if (ingredient.test(this.itemHandler.getStackInSlot(i))) {
                return i;
            }
        }
        return -1;
    }
}
