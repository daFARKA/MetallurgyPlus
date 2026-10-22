package net.dafarka.metallurgyplus.block.multiblock;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class MBEnergyPortBlock extends MBPortBlock {
    public MBEnergyPortBlock(
        Properties properties,
        Supplier<? extends BlockEntityType<? extends BlockEntity>> blockEntityType
    ) {
        super(properties, blockEntityType);
    }
}
