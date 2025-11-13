package net.dafarka.metallurgyplus.util.color;

import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.item.ModItems;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

public class DynamicItemColor implements ItemColor {


    public DynamicItemColor() {}

    @Override
    public int getColor(ItemStack pStack, int pTintIndex) {
        if (pTintIndex == 0) {
            String fullName = pStack.getDescriptionId();
            String name = fullName.split("\\.")[2];
            String type = fullName.split("\\.")[0];
            if (type.equals("item")) {
                if (ModItems.MATERIAL_COLOR_MAP.get(name) != null) {
                    return ModItems.MATERIAL_COLOR_MAP.get(name);
                } else if (ModItems.ORE_COLOR_MAP.get(name) != null){
                    return ModItems.ORE_COLOR_MAP.get(name);
                } else if (ModItems.ALLOY_COLOR_MAP.get(name) != null){
                    return ModItems.ALLOY_COLOR_MAP.get(name);
                } else if (ModItems.VANILLA_MAP.get(name) != null) {
                    return ModItems.VANILLA_COLOR_MAP.get(name);
                }
            } else if (type.equals("block")) {
                if (ModBlocks.MATERIAL_COLOR_MAP.get(name) != null) {
                    return ModBlocks.MATERIAL_COLOR_MAP.get(name);
                } else if (ModBlocks.ORE_COLOR_MAP.get(name) != null) {
                    return ModBlocks.ORE_COLOR_MAP.get(name);
                } else if (ModBlocks.ALLOY_COLOR_MAP.get(name) != null) {
                    return ModBlocks.ALLOY_COLOR_MAP.get(name);
                }
            }

            return -1;
        }
        return -1;
    }
}