package net.dafarka.metallurgyplus.item.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

/** Applies custom Fortune and Silk Touch behavior to modular tools. */
public class ModularToolFortuneLootModifier extends LootModifier {
    private static final Map<String, String> VANILLA_FORTUNE_DROPS = Map.ofEntries(
        Map.entry("coal_ore", "coal"), Map.entry("deepslate_coal_ore", "coal"),
        Map.entry("diamond_ore", "diamond"), Map.entry("deepslate_diamond_ore", "diamond"),
        Map.entry("emerald_ore", "emerald"), Map.entry("deepslate_emerald_ore", "emerald"),
        Map.entry("lapis_ore", "lapis_lazuli"), Map.entry("deepslate_lapis_ore", "lapis_lazuli"),
        Map.entry("iron_ore", "raw_iron"), Map.entry("deepslate_iron_ore", "raw_iron"),
        Map.entry("copper_ore", "raw_copper"), Map.entry("deepslate_copper_ore", "raw_copper"),
        Map.entry("gold_ore", "raw_gold"), Map.entry("deepslate_gold_ore", "raw_gold"),
        Map.entry("nether_gold_ore", "gold_nugget"), Map.entry("nether_quartz_ore", "quartz"),
        Map.entry("redstone_ore", "redstone"), Map.entry("deepslate_redstone_ore", "redstone"),
        Map.entry("amethyst_cluster", "amethyst_shard"), Map.entry("gilded_blackstone", "gold_nugget"),
        Map.entry("glowstone", "glowstone_dust"), Map.entry("sea_lantern", "prismarine_crystals"),
        Map.entry("melon", "melon_slice"), Map.entry("gravel", "flint"),
        Map.entry("short_grass", "wheat_seeds"), Map.entry("fern", "wheat_seeds"),
        Map.entry("tall_grass", "wheat_seeds"), Map.entry("large_fern", "wheat_seeds"),
        Map.entry("wheat", "wheat_seeds"), Map.entry("carrots", "carrot"),
        Map.entry("potatoes", "potato"), Map.entry("beetroots", "beetroot_seeds"),
        Map.entry("nether_wart", "nether_wart"), Map.entry("sweet_berry_bush", "sweet_berries"),
        Map.entry("weeping_vines", "weeping_vines"), Map.entry("weeping_vines_plant", "weeping_vines"),
        Map.entry("twisting_vines", "twisting_vines"), Map.entry("twisting_vines_plant", "twisting_vines")
    );

    public static final Codec<ModularToolFortuneLootModifier> CODEC = RecordCodecBuilder.create(instance ->
        LootModifier.codecStart(instance).apply(instance, ModularToolFortuneLootModifier::new)
    );

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> SERIALIZERS =
        DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MetallurgyPlus.MODID);
    public static final RegistryObject<Codec<ModularToolFortuneLootModifier>> MODULAR_TOOL_FORTUNE =
        SERIALIZERS.register("modular_tool_fortune", () -> CODEC);

    protected ModularToolFortuneLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot,
                                                  LootContext context) {
        ItemStack toolStack = context.getParamOrNull(LootContextParams.TOOL);
        if (toolStack == null || !(toolStack.getItem() instanceof ModularTool tool)) return generatedLoot;
        BlockState blockState = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (blockState == null) return generatedLoot;

        if (tool.hasSilkTouch(toolStack)) {
            return getSilkTouchDrops(generatedLoot, blockState, context);
        }

        float fortuneBonus = tool.getFortuneBonus(toolStack);
        if (fortuneBonus <= 0.0F) return generatedLoot;

        ObjectArrayList<ItemStack> bonusDrops = new ObjectArrayList<>();
        for (ItemStack drop : generatedLoot) {
            if (drop.isEmpty() || !isFortuneAffectedDrop(blockState, drop)) continue;

            float extraCount = drop.getCount() * fortuneBonus;
            int bonusCount = (int) Math.floor(extraCount);
            float fractionalCount = extraCount - bonusCount;
            if (fractionalCount > 0.0F && context.getRandom().nextFloat() < fractionalCount) {
                bonusCount++;
            }

            while (bonusCount > 0) {
                int stackCount = Math.min(bonusCount, drop.getMaxStackSize());
                bonusDrops.add(drop.copyWithCount(stackCount));
                bonusCount -= stackCount;
            }
        }

        generatedLoot.addAll(bonusDrops);
        return generatedLoot;
    }

    private static ObjectArrayList<ItemStack> getSilkTouchDrops(ObjectArrayList<ItemStack> generatedLoot,
                                                                 BlockState blockState,
                                                                 LootContext context) {
        var origin = context.getParamOrNull(LootContextParams.ORIGIN);
        if (origin == null) return generatedLoot;

        BlockPos pos = BlockPos.containing(origin);
        ItemStack silkTouchDrop = blockState.getBlock().getCloneItemStack(
            context.getLevel(), pos, blockState);
        if (silkTouchDrop.isEmpty()) return generatedLoot;

        generatedLoot.clear();
        generatedLoot.add(silkTouchDrop);
        return generatedLoot;
    }

    private static boolean isFortuneAffectedDrop(BlockState state, ItemStack drop) {
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (blockId == null) return false;

        if (blockId.getNamespace().equals("minecraft") && state.is(BlockTags.LEAVES)) {
            return drop.is(ItemTags.SAPLINGS) || drop.is(Items.APPLE) || drop.is(Items.STICK);
        }

        String expectedDrop = blockId.getNamespace().equals("minecraft")
            ? VANILLA_FORTUNE_DROPS.get(blockId.getPath()) : null;
        if (expectedDrop != null) {
            ResourceLocation dropId = ForgeRegistries.ITEMS.getKey(drop.getItem());
            return dropId != null && dropId.getNamespace().equals("minecraft")
                && dropId.getPath().equals(expectedDrop);
        }

        return isMetallurgyPlusOreDrop(blockId, drop);
    }

    private static boolean isMetallurgyPlusOreDrop(ResourceLocation blockId, ItemStack drop) {
        if (!blockId.getNamespace().equals(MetallurgyPlus.MODID)
            || !ModBlocks.ORE_BLOCKS_MAP.containsKey(blockId.getPath())) return false;

        String path = blockId.getPath();
        String oreName;
        if (path.endsWith("_deepslate_block")) {
            oreName = path.substring(0, path.length() - "_deepslate_block".length());
        } else if (path.endsWith("_stone_block")) {
            oreName = path.substring(0, path.length() - "_stone_block".length());
        } else {
            return false;
        }

        RegistryObject<net.minecraft.world.item.Item> rawOre = ModItems.ORE_MAP.get(oreName + "_raw");
        return rawOre != null && drop.is(rawOre.get());
    }
}
