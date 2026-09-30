package net.dafarka.metallurgyplus.block.custom;

import net.dafarka.metallurgyplus.block.entity.ModBlockEntities;
import net.dafarka.metallurgyplus.block.entity.SolarPanelBlockEntity;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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

public class SolarPanelBlock extends BaseBlock {
    public static final int GENERATION = (int) Math.pow(2, 6);

    private int tier = 0;

    public SolarPanelBlock(Properties pProperties, int tier) {
        super(pProperties);

        this.tier = tier;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof SolarPanelBlockEntity) {
                ((SolarPanelBlockEntity) blockEntity).drops();
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

        return createTickerHelper(pBlockEntityType, ModBlockEntities.SOLAR_BLOCK_ENTITIES.get(tier).get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SolarPanelBlockEntity(pPos, pState, tier);
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        int generation = GENERATION * (int) Math.pow(2, tier - 1);
        if (tier == 26) generation = Integer.MAX_VALUE;
        tooltip.add(Component.literal("Generates " + Utility.formatWithSeparator(generation, ',') + " FE/t").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
    }
}
