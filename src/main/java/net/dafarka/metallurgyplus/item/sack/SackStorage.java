package net.dafarka.metallurgyplus.item.sack;

import net.dafarka.metallurgyplus.item.custom.SackItem;
import net.dafarka.metallurgyplus.util.ModTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class SackStorage {

    private static final String CONTENTS_TAG = "SackContents";

    /**
     * Checks whether an ItemStack is of SackItem.
     */
    public static boolean isSack(ItemStack stack) {
        return stack.getItem() instanceof SackItem;
    }

    /**
     * Returns the maximum amount of each individual item
     * that can be stored in this sack.
     */
    public static int getCapacity(ItemStack sack) {
        if (!(sack.getItem() instanceof SackItem sackItem)) {
            return 0;
        }

        return sackItem.getCapacity();
    }

    /**
     * Checks whether an item is allowed to be stored in a sack.
     */
    public static boolean canStore(Item item) {
        return item.builtInRegistryHolder().is(ModTags.Items.MATERIAL_RAW);
    }

    /**
     * Checks whether an ItemStack is allowed to be stored in a sack.
     */
    public static boolean canStore(ItemStack stack) {
        return !stack.isEmpty() && canStore(stack.getItem());
    }

    /**
     * Returns the amount of a specific item currently stored.
     */
    public static int getAmount(ItemStack sack, Item item) {
        if (!isSack(sack)) return 0;

        CompoundTag contents = getContentsTag(sack);

        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);

        if (id == null) {
            return 0;
        }

        return contents.getInt(id.toString());
    }

    /**
     * Returns how much additional space is available for a specific item.
     * <p>
     * Capacity is PER ITEM, not shared between all items.
     */
    public static int getSpace(ItemStack sack, Item item) {
        if (!isSack(sack)) return 0;

        if (!canStore(item)) {
            return 0;
        }

        return Math.max(
                0,
                getCapacity(sack) - getAmount(sack, item)
        );
    }

    /**
     * Adds as much of the item as possible.
     *
     * @return the amount that was actually added.
     */
    public static int add(ItemStack sack, Item item, int amount) {
        if (!isSack(sack)) return 0;

        if (!canStore(item) || amount <= 0) {
            return 0;
        }

        int space = getSpace(sack, item);
        int amountToAdd = Math.min(space, amount);

        if (amountToAdd > 0) {
            setAmount(
                    sack,
                    item,
                    getAmount(sack, item) + amountToAdd
            );
        }

        return amountToAdd;
    }

    /**
     * Adds as much of the given ItemStack as possible.
     *
     * @return the amount that was actually added.
     */
    public static int add(ItemStack sack, ItemStack stack) {
        if (!isSack(sack)) return 0;

        if (stack.isEmpty()) {
            return 0;
        }

        return add(
                sack,
                stack.getItem(),
                stack.getCount()
        );
    }

    /**
     * Removes up to the requested amount of an item.
     *
     * @return the amount that was removed.
     */
    public static int remove(ItemStack sack, Item item, int amount) {
        if (!isSack(sack)) return 0;

        if (!canStore(item) || amount <= 0) {
            return 0;
        }

        int current = getAmount(sack, item);
        int amountToRemove = Math.min(current, amount);

        if (amountToRemove > 0) {
            setAmount(sack, item, current - amountToRemove);
        }

        return amountToRemove;
    }

    /**
     * Sets the amount of a specific item.
     * <p>
     * The amount is automatically clamped to the sack's per-item capacity.
     */
    public static void setAmount(ItemStack sack, Item item, int amount) {
        if (!isSack(sack)) {
            return;
        }

        if (!canStore(item)) {
            return;
        }

        amount = Math.max(
                0,
                Math.min(amount, getCapacity(sack))
        );

        CompoundTag contents = getContentsTag(sack);
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);

        if (id == null) {
            return;
        }

        if (amount <= 0) {
            contents.remove(id.toString());
        } else {
            contents.putInt(id.toString(), amount);
        }

        sack.getOrCreateTag().put(CONTENTS_TAG, contents);
    }

    /**
     * Returns all currently stored items and their amounts.
     */
    public static Map<Item, Integer> getContents(ItemStack sack) {
        Map<Item, Integer> result = new HashMap<>();

        if (!isSack(sack)) {
            return result;
        }

        CompoundTag contents = getContentsTag(sack);

        for (String key : contents.getAllKeys()) {
            ResourceLocation id = ResourceLocation.tryParse(key);

            if (id == null) {
                continue;
            }

            Item item = ForgeRegistries.ITEMS.getValue(id);

            if (item != null && canStore(item)) {
                int amount = contents.getInt(key);

                if (amount > 0) {
                    result.put(item, amount);
                }
            }
        }

        return result;
    }

    private static CompoundTag getContentsTag(ItemStack sack) {
        return sack.getOrCreateTag().getCompound(CONTENTS_TAG);
    }
}
