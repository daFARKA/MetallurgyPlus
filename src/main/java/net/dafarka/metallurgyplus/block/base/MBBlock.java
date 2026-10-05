package net.dafarka.metallurgyplus.block.base;

import net.dafarka.metallurgyplus.block.entity.multiblock.MBControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MBBlock extends BaseBlock {
    private final Supplier<? extends BlockEntityType<? extends MBControllerBlockEntity>> blockEntityType;

    public MBBlock(
        Properties properties,
        Supplier<? extends BlockEntityType<? extends MBControllerBlockEntity>> blockEntityType
    ) {
        super(properties);
        this.blockEntityType = blockEntityType;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityType.get().create(pos, state);
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
