package net.dafarka.metallurgyplus.item.tool;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * An item used as one of the interchangeable parts of a modular tool.
 */
public class ModularToolPartItem extends Item {
    public enum PartType {
        HEAD,
        ROD
    }

    private final PartType partType;
    private final int baseDurability;
    private final float durabilityModifier;
    private final float miningSpeed;
    @Nullable
    private final ResourceLocation repairMaterialId;

    private ModularToolPartItem(PartType partType, int baseDurability, float durabilityModifier,
                                float miningSpeed, @Nullable ResourceLocation repairMaterialId,
                                Properties properties) {
        super(properties);
        this.partType = partType;
        this.baseDurability = baseDurability;
        this.durabilityModifier = durabilityModifier;
        this.miningSpeed = miningSpeed;
        this.repairMaterialId = repairMaterialId;
    }

    public static ModularToolPartItem head(int baseDurability, float miningSpeed) {
        return head(baseDurability, miningSpeed, null);
    }

    public static ModularToolPartItem head(int baseDurability, float miningSpeed,
                                           @Nullable ResourceLocation repairMaterialId) {
        return new ModularToolPartItem(PartType.HEAD, baseDurability, 1.0F, miningSpeed,
            repairMaterialId, new Properties());
    }

    public static ModularToolPartItem rod(float durabilityModifier) {
        return new ModularToolPartItem(PartType.ROD, 0, durabilityModifier, 0.0F,
            null, new Properties());
    }

    public PartType getPartType() {
        return partType;
    }

    public int getBaseDurability() {
        return baseDurability;
    }

    public float getDurabilityModifier() {
        return durabilityModifier;
    }

    public float getMiningSpeed() {
        return miningSpeed;
    }

    @Nullable
    public ResourceLocation getRepairMaterialId() {
        return repairMaterialId;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (partType == PartType.HEAD) {
            tooltip.add(Component.literal("Base durability: " + baseDurability)
                .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("Mining speed: " + format(miningSpeed))
                .withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.literal("Durability modifier: ×" + format(durabilityModifier))
                .withStyle(ChatFormatting.GRAY));
        }
    }

    private static String format(float value) {
        return String.format(java.util.Locale.ROOT, "%.2f", value);
    }
}
