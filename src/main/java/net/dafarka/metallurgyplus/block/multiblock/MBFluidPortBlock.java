package net.dafarka.metallurgyplus.block.multiblock;

import net.dafarka.metallurgyplus.block.entity.multiblock.MBFluidPortBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

public class MBFluidPortBlock extends MBPortBlock {
    public MBFluidPortBlock(
        Properties properties,
        Supplier<? extends BlockEntityType<? extends BlockEntity>> blockEntityType
    ) {
        super(properties, blockEntityType);
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
        if (!level.isClientSide
            && level.getBlockEntity(pos) instanceof MBFluidPortBlockEntity fluidPort
            && fluidPort.interactWithFluidContainer(player, hand)) {
            return InteractionResult.CONSUME;
        }

        return super.use(state, level, pos, player, hand, hit);
    }
}
