package net.dafarka.metallurgyplus.util.color;

import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.item.ModItems;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

public class DynamicSackColor implements ItemColor {

    public DynamicSackColor() {}

    @Override
    public int getColor(ItemStack pStack, int pTintIndex) {
        if (pTintIndex == 0) {
            String fullName = pStack.getDescriptionId();
            String name = fullName.split("\\.")[2];
            int tier = Integer.parseInt(name.replaceAll("\\D+", ""));

            return ModItems.SACK_COLOR_MAP.get(tier) != null ? ModItems.SACK_COLOR_MAP.get(tier) : -1;
        }

        return -1;
    }
}
