package net.dafarka.metallurgyplus.block.custom;

import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.math.BigInteger;
import java.util.List;

public class BatteryCellBlock extends Block {

    private static final BigInteger BASE_CAPACITY = BigInteger.valueOf(1_000_000_000L);

    private final int tier;

    public BatteryCellBlock(BlockBehaviour.Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    public static BigInteger getCapacityForTier(int tier) {
        return getTierAmount(BASE_CAPACITY, tier);
    }

    private static BigInteger getTierAmount(BigInteger baseAmount, int tier) {
        return baseAmount.multiply(BigInteger.TEN.pow(tier - 1));
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable(
            "tooltip.metallurgyplus.battery_cell_capacity",
            Utility.formatCompact(getCapacityForTier(tier))
        ).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
}
