package net.dafarka.metallurgyplus.block.entity.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity marker for multiblock energy ports. Energy transfer is coordinated by the
 * multiblock controller, which owns the structure and its energy storage.
 */
public class MBEnergyPortBlockEntity extends MBPortBlockEntity {
    public MBEnergyPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
