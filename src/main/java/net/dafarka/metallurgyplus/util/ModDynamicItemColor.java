package net.dafarka.metallurgyplus.util;

import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.item.ModItems;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

public class ModDynamicItemColor implements ItemColor {


    public ModDynamicItemColor() {}

    @Override
    public int getColor(ItemStack pStack, int pTintIndex) {
        if (pTintIndex == 0) {
            // Return the color for the contents based on item type
            String fullName = pStack.getDescriptionId();
            String name = fullName.split("\\.")[2];
            if (ModItems.COLOR_MAP.get(name) != null) {
                return ModItems.COLOR_MAP.get(name);
            } else {
                if (ModBlocks.COLOR_MAP.get(name) != null) {
                    return ModBlocks.COLOR_MAP.get(name);
                }
            }
            return -1;
        }
        return -1; // -1 if nothing should change
    }
}