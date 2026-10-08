package net.dafarka.metallurgyplus.block.base;

import net.dafarka.metallurgyplus.block.entity.multiblock.MBControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
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

    @Override
    public InteractionResult use(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hit
    ) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MenuProvider menuProvider) {
                NetworkHooks.openScreen(serverPlayer, menuProvider, pos);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
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
