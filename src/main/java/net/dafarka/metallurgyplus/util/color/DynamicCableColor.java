package net.dafarka.metallurgyplus.util.color;

import net.dafarka.metallurgyplus.block.ModBlocks;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DynamicCableColor implements BlockColor, ItemColor {

    public DynamicCableColor() {}

    @Override
    public int getColor(BlockState pState, @Nullable BlockAndTintGetter pLevel, @Nullable BlockPos pPos, int pTintIndex) {
        if (pTintIndex == 0) {
            String fullName = pState.getBlock().getDescriptionId();
            int tier = Integer.parseInt(fullName.split("\\.")[2].replaceAll("\\D+", ""));
            return ModBlocks.CABLE_COLOR_MAP.get(tier) != null ? ModBlocks.CABLE_COLOR_MAP.get(tier) : -1;
        }
        return -1;
    }

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
