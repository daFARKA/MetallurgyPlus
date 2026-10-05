package net.dafarka.metallurgyplus.block.custom;

import net.dafarka.metallurgyplus.block.base.BaseBlock;
import net.dafarka.metallurgyplus.block.entity.BatteryBlockEntity;
import net.dafarka.metallurgyplus.block.entity.ModBlockEntities;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
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
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BatteryBlock extends BaseBlock {

    public static final int CAPACITY = 10000;

    private int tier = 0;

    public BatteryBlock(Properties pProperties, int tier) {
        super(pProperties);

        this.tier = tier;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof BatteryBlockEntity batteryBlockEntity) {
                ItemStack stack = new ItemStack(this);

                CompoundTag nbt = new CompoundTag();
                batteryBlockEntity.saveToItem(nbt);
                stack.getOrCreateTag().put("BlockEntityTag", nbt);

                popResource(pLevel, pPos, stack);
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof BatteryBlockEntity) {
                NetworkHooks.openScreen((ServerPlayer) pPlayer, (MenuProvider) entity, pPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, ModBlockEntities.BATTERY_BLOCK_ENTITIES.get(tier).get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BatteryBlockEntity(pPos, pState, tier);
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag")) {
            CompoundTag beTag = stack.getTag().getCompound("BlockEntityTag");

            if (beTag.contains("energy")) {
                CompoundTag energyTag = beTag.getCompound("energy");

                int energy = energyTag.getInt("energy");

                tooltip.add(Component.literal("Energy: " + Utility.formatWithSeparator(energy, ',') + " FE")
                    .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
                return;
            }
        }

        tooltip.add(Component.literal("Energy: 0 FE")
            .withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));

        int capacity = BatteryBlock.CAPACITY * (int) Math.pow(10, tier - 1);
        if (tier == 7) capacity = Integer.MAX_VALUE;
        double transfer_d = BatteryBlock.CAPACITY * Math.pow(10, tier - 2);
        int transfer = (int) transfer_d;
        tooltip.add(Component.literal("Max Capacity " + Utility.formatWithSeparator(capacity, ',') + " FE/t").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        tooltip.add(Component.literal("Max Transfer " + Utility.formatWithSeparator(transfer, ',') + " FE/t").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
    }
}
