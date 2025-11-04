package net.dafarka.metallurgyplus.util;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class Utility {

    /**
     * Gets an item from ForgeRegistries using the name.
     * <br>
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
     * Formats a given name from "type.id.resource" to "id:resource"
     * <br>
     * name is not checked for proper formatting!
     *
     * @param name The input name which should always be of the form "type.id.resource"
     *
     * @return The formatted name of the form "id:resource"
     * */
    public String formatResourceName(String name) {
        String[] parts = name.split("\\.");
        return parts[parts.length - 2] + ":" + parts[parts.length - 1];
    }

    /**
     * Formats a given Integer to have digit grouping {@code separator}.
     *
     * @param number the Integer number to format
     * @param separator the separator symbol between the digit groups.
     *
     * @return The formatted string with the corresponding digit grouping
     */
    public static String formatWithSeparator(@NotNull Integer number, char separator) {
        String numStr = number.toString();
        StringBuilder sb = new StringBuilder();

        int len = numStr.length();
        int count = 0;

        // Handle negative numbers
        int start = 0;
        if (numStr.charAt(0) == '-') {
            sb.append('-');
            start = 1;
        }

        // Insert separator every 3 digits from the end
        for (int i = len - 1; i >= start; i--) {
            sb.insert(start, numStr.charAt(i));
            count++;
            if (count % 3 == 0 && i > start) {
                sb.insert(start, separator);
            }
        }

        return sb.toString();
    }
}
