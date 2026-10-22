package net.dafarka.metallurgyplus.block.multiblock;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class MBPortBlock extends MBBlock {
    public MBPortBlock(
        Properties properties,
        Supplier<? extends BlockEntityType<? extends BlockEntity>> blockEntityType
    ) {
        super(properties, blockEntityType);
    }
}
