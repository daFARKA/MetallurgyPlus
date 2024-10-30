package net.dafarka.metallurgyplus.util;

import net.dafarka.metallurgyplus.block.ModBlocks;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ModDynamicBlockColor implements BlockColor {


    public ModDynamicBlockColor() {}

    @Override
    public int getColor(BlockState pState, @Nullable BlockAndTintGetter pLevel, @Nullable BlockPos pPos, int pTintIndex) {
        if (pTintIndex == 0) {
            String fullName = pState.getBlock().getDescriptionId();
            return ModBlocks.COLOR_MAP.get(fullName.split("\\.")[2]) != null ? ModBlocks.COLOR_MAP.get(fullName.split("\\.")[2]) : -1;
        }
        return -1;
    }
}