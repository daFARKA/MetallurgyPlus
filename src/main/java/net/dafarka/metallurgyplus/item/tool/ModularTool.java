package net.dafarka.metallurgyplus.item.tool;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * Shared component storage, durability, wear, and tooltip behavior for modular tools.
 */
public abstract class ModularTool extends DiggerItem {
    public static final String HEAD_ID_TAG = "MetallurgyPlusHead";
    public static final String ROD_ID_TAG = "MetallurgyPlusRod";
    public static final String MAX_DURABILITY_TAG = "MetallurgyPlusMaxDurability";
    public static final String CUSTOM_DAMAGE_TAG = "MetallurgyPlusDamage";
    public static final String MINING_SPEED_BONUS_TAG = "MetallurgyPlusMiningSpeedBonus";
    public static final String FORTUNE_BONUS_TAG = "MetallurgyPlusFortuneBonus";
    public static final String SILK_TOUCH_TAG = "MetallurgyPlusSilkTouch";
    public static final String LEVEL_TAG = "MetallurgyPlusLevel";
    public static final String EXPERIENCE_TAG = "MetallurgyPlusExperience";
    private static final String MODIFIERS_TAG = "MetallurgyPlusModifiers";

    public static final float MAX_FORTUNE_BONUS = 3.0F;

    private final ResourceLocation defaultHeadId;
    private final ResourceLocation defaultRodId;

    protected ModularTool(float attackDamage, float attackSpeed, Tier tier, TagKey<Block> mineableBlocks,
                          ResourceLocation defaultHeadId, ResourceLocation defaultRodId,
                          Item.Properties properties) {
        super(attackDamage, attackSpeed, tier, mineableBlocks, properties.stacksTo(1));
        this.defaultHeadId = defaultHeadId;
        this.defaultRodId = defaultRodId;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    public ItemStack createConfiguredStack(ResourceLocation headId, ResourceLocation rodId) {
        ItemStack stack = new ItemStack(this);
        setComponents(stack, headId, rodId);
        return stack;
    }

    public ItemStack createConfiguredStack(ItemStack head, ItemStack rod) {
        ResourceLocation headId = ForgeRegistries.ITEMS.getKey(head.getItem());
        ResourceLocation rodId = ForgeRegistries.ITEMS.getKey(rod.getItem());
        return createConfiguredStack(headId, rodId);
    }

    /**
     * Stores the selected parts and snapshots the durability they provide.
     */
    public void setComponents(ItemStack stack, ResourceLocation headId, ResourceLocation rodId) {
        ResourceLocation safeHeadId = isPart(headId, ModularToolPartItem.PartType.HEAD)
            ? headId : defaultHeadId;
        ResourceLocation safeRodId = isPart(rodId, ModularToolPartItem.PartType.ROD)
            ? rodId : defaultRodId;

        var tag = stack.getOrCreateTag();
        tag.putString(HEAD_ID_TAG, safeHeadId.toString());
        tag.putString(ROD_ID_TAG, safeRodId.toString());
        tag.putInt(MAX_DURABILITY_TAG, calculateMaxDurability(safeHeadId, safeRodId));
        tag.putInt(CUSTOM_DAMAGE_TAG, 0);
        tag.putFloat(MINING_SPEED_BONUS_TAG, 0.0F);
    }

    public ResourceLocation getHeadId(ItemStack stack) {
        return getSavedId(stack, HEAD_ID_TAG, defaultHeadId);
    }

    public ResourceLocation getRodId(ItemStack stack) {
        return getSavedId(stack, ROD_ID_TAG, defaultRodId);
    }

    public int getMaxDurability(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(MAX_DURABILITY_TAG)) {
            int saved = stack.getTag().getInt(MAX_DURABILITY_TAG);
            if (saved > 0) return saved;
        }
        return calculateMaxDurability(getHeadId(stack), getRodId(stack));
    }

    public int getCustomDamage(ItemStack stack) {
        if (!stack.hasTag()) return 0;
        return Mth.clamp(stack.getTag().getInt(CUSTOM_DAMAGE_TAG), 0, getMaxDurability(stack));
    }

    public void repair(ItemStack stack, int amount) {
        if (amount <= 0) return;
        stack.getOrCreateTag().putInt(CUSTOM_DAMAGE_TAG,
            Math.max(0, getCustomDamage(stack) - amount));
    }

    public float getMiningSpeedBonus(ItemStack stack) {
        return stack.hasTag() ? Math.max(0.0F, stack.getTag().getFloat(MINING_SPEED_BONUS_TAG)) : 0.0F;
    }

    public float getMiningSpeed(ItemStack stack) {
        return getMiningSpeed(getHeadId(stack)) + getMiningSpeedBonus(stack);
    }

    public void addMiningSpeedBonus(ItemStack stack, float amount) {
        if (amount <= 0.0F) return;
        stack.getOrCreateTag().putFloat(MINING_SPEED_BONUS_TAG, getMiningSpeedBonus(stack) + amount);
    }

    public float getFortuneBonus(ItemStack stack) {
        if (hasSilkTouch(stack)) return 0.0F;
        return getStoredFortuneBonus(stack);
    }

    public void addFortuneBonus(ItemStack stack, float amount) {
        if (amount <= 0.0F || hasSilkTouch(stack)) return;
        float updatedBonus = Math.min(MAX_FORTUNE_BONUS, getFortuneBonus(stack) + amount);
        stack.getOrCreateTag().putFloat(FORTUNE_BONUS_TAG, updatedBonus);
    }

    public boolean hasSilkTouch(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(SILK_TOUCH_TAG);
    }

    public boolean addSilkTouch(ItemStack stack) {
        if (getStoredFortuneBonus(stack) > 0.0F) return false;
        stack.getOrCreateTag().putBoolean(SILK_TOUCH_TAG, true);
        return true;
    }

    private float getStoredFortuneBonus(ItemStack stack) {
        return stack.hasTag()
            ? Mth.clamp(stack.getTag().getFloat(FORTUNE_BONUS_TAG), 0.0F, MAX_FORTUNE_BONUS)
            : 0.0F;
    }

    public int getLevel(ItemStack stack) {
        return stack.hasTag() ? Math.max(0, stack.getTag().getInt(LEVEL_TAG)) : 0;
    }

    public int getModifiers(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(MODIFIERS_TAG)
            ? Math.max(0, stack.getTag().getInt(MODIFIERS_TAG)) : 3;
    }

    public void setModifiers(ItemStack stack, int modifiers) {
        if (modifiers < 0) return;
        stack.getOrCreateTag().putInt(MODIFIERS_TAG, modifiers);
    }

    /**
     * Advances this tool stack by one level and clears its progress toward the next level.
     */
    public int upgradeLevel(ItemStack stack) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 2.0F));

        var tag = stack.getOrCreateTag();
        int newLevel = getLevel(stack) + 1;
        tag.putInt(LEVEL_TAG, newLevel);
        tag.putInt(EXPERIENCE_TAG, 0);
        tag.putInt(MODIFIERS_TAG, getModifiers(stack) + 1);
        return newLevel;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos,
                             LivingEntity miner) {
        if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F) {
            applyCustomDamage(stack, 1, miner);
        }
        return true;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide) {
            applyCustomDamage(stack, 2, attacker);
        }
        return true;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getCustomDamage(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        float remaining = 1.0F - (float) getCustomDamage(stack) / getMaxDurability(stack);
        return Mth.clamp(Math.round(remaining * 13.0F), 0, 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float remaining = 1.0F - (float) getCustomDamage(stack) / getMaxDurability(stack);
        return Mth.hsvToRgb(Math.max(0.0F, remaining) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        int maxDurability = getMaxDurability(stack);
        tooltip.add(Component.literal("Durability: " + (maxDurability - getCustomDamage(stack))
            + " / " + maxDurability).withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.literal("Experience: " + getExperience(stack) + " / " +
            calculateExperienceNeeded(getLevel(stack))).withStyle(ChatFormatting.GREEN));

        tooltip.add(Component.empty());

        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.literal("<Shift>")
                .withStyle(ChatFormatting.WHITE)
                .withStyle(ChatFormatting.ITALIC));
            return;
        }

        ResourceLocation headId = getHeadId(stack);
        ResourceLocation rodId = getRodId(stack);

        tooltip.add(Component.literal("Level: " + getLevel(stack)).withStyle(ChatFormatting.DARK_GREEN));
        tooltip.add(Component.literal("Modifiers: " + getModifiers(stack)).withStyle(ChatFormatting.DARK_AQUA));
        tooltip.add(Component.literal("Head: ").append(itemName(headId)).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Rod: ").append(itemName(rodId)).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Mining Speed: " + getMiningSpeed(stack)).withStyle(ChatFormatting.GRAY));

        if (getFortuneBonus(stack) > 0.0F) {
            tooltip.add(Component.literal("Fortune: " + String.format(Locale.ROOT, "%.2f", getFortuneBonus(stack)))
                .withStyle(ChatFormatting.GRAY));
        }
        if (hasSilkTouch(stack)) {
            tooltip.add(Component.literal("Silk Touch").withStyle(ChatFormatting.GRAY));
        }
    }

    private void applyCustomDamage(ItemStack stack, int amount, LivingEntity entity) {
        if (amount <= 0 || (entity instanceof Player player && player.getAbilities().instabuild)) {
            return;
        }

        int maxDurability = getMaxDurability(stack);
        int currentDamage = getCustomDamage(stack);
        if (currentDamage >= maxDurability) return;

        int damage = Math.min(currentDamage + amount, maxDurability);
        stack.getOrCreateTag().putInt(CUSTOM_DAMAGE_TAG, damage);
        if (damage >= maxDurability) {
            entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        }

        applyExperience(stack, amount);
    }

    private void applyExperience(ItemStack stack, int amount) {
        var tag = stack.getOrCreateTag();
        int experience = getExperience(stack) + amount;
        int level = getLevel(stack);

        if (experience >= calculateExperienceNeeded(level)) {
            upgradeLevel(stack);
        }
        tag.putInt(EXPERIENCE_TAG, experience);
    }

    private int getExperience(ItemStack stack) {
        return stack.hasTag() ? Math.max(0, stack.getTag().getInt(EXPERIENCE_TAG)) : 0;
    }

    private int calculateExperienceNeeded(int level) {
        return (int) Math.pow(2, (7 + level));
    }

    private boolean isPart(ResourceLocation id, ModularToolPartItem.PartType type) {
        if (id == null) return false;
        Item item = ForgeRegistries.ITEMS.getValue(id);
        return item instanceof ModularToolPartItem part && part.getPartType() == type;
    }

    private ModularToolPartItem getPartOrDefault(ResourceLocation id, ModularToolPartItem.PartType type, ResourceLocation fallbackId) {
        ModularToolPartItem part = getPart(id, type);
        return part != null ? part : getPart(fallbackId, type);
    }

    @Nullable
    private static ModularToolPartItem getPart(ResourceLocation id, ModularToolPartItem.PartType type) {
        if (id == null) return null;
        Item item = ForgeRegistries.ITEMS.getValue(id);
        return item instanceof ModularToolPartItem part && part.getPartType() == type ? part : null;
    }

    private int calculateMaxDurability(ResourceLocation headId, ResourceLocation rodId) {
        ModularToolPartItem head = getPartOrDefault(headId, ModularToolPartItem.PartType.HEAD, defaultHeadId);
        ModularToolPartItem rod = getPartOrDefault(rodId, ModularToolPartItem.PartType.ROD, defaultRodId);
        int headDurability = head == null ? 250 : head.getBaseDurability();
        float rodModifier = rod == null ? 1.0F : rod.getDurabilityModifier();
        return Math.max(1, Math.round(headDurability * rodModifier));
    }

    private float getMiningSpeed(ResourceLocation headId) {
        ModularToolPartItem head = getPartOrDefault(headId, ModularToolPartItem.PartType.HEAD, defaultHeadId);
        return head.getMiningSpeed();
    }

    private static ResourceLocation getSavedId(ItemStack stack, String key,
                                               ResourceLocation fallback) {
        if (!stack.hasTag() || !stack.getTag().contains(key)) return fallback;
        ResourceLocation parsed = ResourceLocation.tryParse(stack.getTag().getString(key));
        return parsed == null ? fallback : parsed;
    }

    private static Component itemName(ResourceLocation itemId) {
        return Component.translatable("item." + itemId.getNamespace() + "." + itemId.getPath());
    }
}
