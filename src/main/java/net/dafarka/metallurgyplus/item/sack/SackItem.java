package net.dafarka.metallurgyplus.item.sack;

import net.dafarka.metallurgyplus.screen.menu.SackMenu;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class SackItem extends Item {

    private final int tier;
    private final int capacity;

    public SackItem(Properties properties, int tier) {
        super(properties);

        this.tier = tier;

        int capacity = 1000 * (int) Math.pow(10, tier - 1);
        if (tier == 8) capacity = Integer.MAX_VALUE;
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getTier() {
        return tier;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {

            int slot;
            boolean offhand;

            if (hand == InteractionHand.OFF_HAND) {
                slot = -1;
                offhand = true;
            } else {
                slot = player.getInventory().selected;
                offhand = false;
            }

            NetworkHooks.openScreen(
                serverPlayer,
                new SimpleMenuProvider(
                    (containerId, inventory, playerEntity) -> new SackMenu(containerId, inventory, slot, offhand),
                    Component.literal("Sack")
                ),
                buffer -> {
                    buffer.writeInt(slot);
                    buffer.writeBoolean(offhand);
                }
            );
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal("Capacity: " + Utility.formatWithSeparator(capacity, ',') + " per Item")
            .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
    }
}