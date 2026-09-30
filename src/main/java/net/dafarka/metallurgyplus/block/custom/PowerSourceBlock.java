package net.dafarka.metallurgyplus.block.custom;

import net.dafarka.metallurgyplus.block.entity.ModBlockEntities;
import net.dafarka.metallurgyplus.block.entity.PowerSourceBlockEntity;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PowerSourceBlock extends BaseBlock {

    public PowerSourceBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof PowerSourceBlockEntity) {
                ((PowerSourceBlockEntity) blockEntity).drops();
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

        return createTickerHelper(pBlockEntityType, ModBlockEntities.POWER_SOURCE_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new PowerSourceBlockEntity(pPos, pState);
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal("Generates " + Utility.formatWithSeparator(Integer.MAX_VALUE, ',') + " FE/t").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
    }
}
