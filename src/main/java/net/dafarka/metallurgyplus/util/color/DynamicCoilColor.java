package net.dafarka.metallurgyplus.util.color;

import net.dafarka.metallurgyplus.block.ModBlocks;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DynamicCoilColor implements ItemColor {

    public DynamicCoilColor() {}

    @Override
    public int getColor(ItemStack pStack, int pTintIndex) {
        if (pTintIndex == 0) {
            String fullName = pStack.getDescriptionId();
            String name = fullName.split("\\.")[2];
            int tier = Integer.parseInt(name.replaceAll("\\D+", ""));

            return ModBlocks.CABLE_COLOR_MAP.get(tier) != null ? ModBlocks.CABLE_COLOR_MAP.get(tier) : -1;
        }

        return -1;
    }
}
