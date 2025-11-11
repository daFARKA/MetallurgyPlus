package net.dafarka.metallurgyplus.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ModTools extends TieredItem {
    private Component name;
    private float speedMultiplier;
    private boolean alwaysCorrectTool;


    public ModTools(Tier tier, Item.Properties properties, Component name, float speedMultiplier, boolean alwaysCorrectTool) {
        super(tier, properties);
        this.name = name;
        this.speedMultiplier = speedMultiplier;
        this.alwaysCorrectTool = alwaysCorrectTool;
    }

    public boolean isCorrectToolForDrops(BlockState state) {
        return alwaysCorrectTool;
    }

    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return speedMultiplier;
    }

    @Override
    public Component getName(ItemStack stack) {
        return name;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        if (!stack.hasTag() || !stack.getOrCreateTag().getBoolean("Unbreakable")) {
            stack.getOrCreateTag().putBoolean("Unbreakable", true);
            stack.getOrCreateTag().putInt("HideFlags", 4);
        }
        super.inventoryTick(stack, level, entity, slot, selected);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @Nullable Level level,
                                @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {

        if (!stack.isDamageableItem()) {
            tooltip.add(Component.literal("Unbreakable").withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD));
        }
        if (alwaysCorrectTool) {
            tooltip.add(Component.literal("Can mine any block").withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC));
        }

        super.appendHoverText(stack, level, tooltip, flag);
    }
}
