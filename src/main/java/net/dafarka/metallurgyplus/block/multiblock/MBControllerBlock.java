package net.dafarka.metallurgyplus.block.multiblock;

import net.dafarka.metallurgyplus.block.entity.multiblock.MBControllerBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MBControllerBlock extends MBBlock {

    private final Supplier<? extends BlockEntityType<? extends MBControllerBlockEntity>> blockEntityType;

    public MBControllerBlock(Properties properties, Supplier<? extends BlockEntityType<? extends MBControllerBlockEntity>> blockEntityType) {
        super(properties, blockEntityType);

        this.blockEntityType = blockEntityType;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        Level level,
        BlockState state,
        BlockEntityType<T> type
    ) {
        if (level.isClientSide) {
            return null;
        }

        if (type != blockEntityType.get()) {
            return null;
        }

        return (tickerLevel, pos, tickerState, blockEntity) -> {
            if (blockEntity instanceof MBControllerBlockEntity controller) {
                MBControllerBlockEntity.tick(tickerLevel, pos, tickerState, controller);
            }
        };
    }
}
