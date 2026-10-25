package net.dafarka.metallurgyplus.block.custom;

import net.dafarka.metallurgyplus.block.entity.CableBlockEntity;
import net.dafarka.metallurgyplus.block.entity.ModBlockEntities;
import net.dafarka.metallurgyplus.block.multiblock.MBEnergyPortBlock;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.List;

public class CableBlock extends PipeBlock implements EntityBlock {
    public static final int TRANSFER = 1000;

    private int tier;

    public CableBlock(Properties pProperties, int tier) {
        super(0.125f, pProperties);

        this.tier = tier;
    }

    public BigInteger getTransfer() {
        return CableBlockEntity.getTransfer(tier);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof CableBlockEntity) {
                ((CableBlockEntity) blockEntity).drops();
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }

        BlockEntityType<CableBlockEntity> expected = ModBlockEntities.CABLE_BLOCK_ENTITIES.get(tier).get();

        if (pBlockEntityType != expected) {
            return null;
        }

        return (level, pos, state, blockEntity) -> {
            if (blockEntity instanceof CableBlockEntity cable) {
                cable.tick(level, pos, state);
            }
        };
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CableBlockEntity(pPos, pState, tier);
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal("Transfers " + Utility.formatCompact(getTransfer()) + " FE/t").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return this.defaultBlockState()
            .setValue(DOWN, canConnectTo(level, pos.below(), Direction.UP))
            .setValue(UP, canConnectTo(level, pos.above(), Direction.DOWN))
            .setValue(NORTH, canConnectTo(level, pos.north(), Direction.SOUTH))
            .setValue(SOUTH, canConnectTo(level, pos.south(), Direction.NORTH))
            .setValue(WEST, canConnectTo(level, pos.west(), Direction.EAST))
            .setValue(EAST, canConnectTo(level, pos.east(), Direction.WEST));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        boolean connect = canConnectTo(level, neighborPos, facing.getOpposite());
        return state.setValue(PROPERTY_BY_DIRECTION.get(facing), connect);
    }

    private boolean canConnectTo(BlockGetter level, BlockPos neighborPos, Direction directionFromNeighbor) {
        BlockState neighborState = level.getBlockState(neighborPos);

        // Connect to other cables
        if (neighborState.getBlock() instanceof CableBlock) {
            return true;
        }

        // The formed controller exchanges energy with the adjacent cable network through the energy port block entity.
        if (neighborState.getBlock() instanceof MBEnergyPortBlock) {
            return true;
        }

        // Connect to capability targets
        if (level instanceof Level actualLevel) {
            var blockEntity = actualLevel.getBlockEntity(neighborPos);
            if (blockEntity != null) {
                return blockEntity.getCapability(
                    ForgeCapabilities.ENERGY,
                    directionFromNeighbor
                ).isPresent();
            }
        }
        return false;
    }
}
