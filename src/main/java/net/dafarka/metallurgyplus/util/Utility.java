package net.dafarka.metallurgyplus.util;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class Utility {

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
     * Formats a given name from "type.id.resource" to "id:resource"
     *
     * name is not checked for proper formatting!
     *
     * @param name The input name which should always be of the form "type.id.resource"
     *
     * @return The formatted name of the form "id:resource"
     * */
    public String formatName(String name) {
        String[] parts = name.split("\\.");
        return parts[parts.length - 2] + ":" + parts[parts.length - 1];
    }
}
