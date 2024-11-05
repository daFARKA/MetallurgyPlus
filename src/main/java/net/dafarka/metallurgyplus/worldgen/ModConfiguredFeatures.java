package net.dafarka.metallurgyplus.worldgen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModConfiguredFeatures {
    public static final Map<String, ResourceKey<ConfiguredFeature<?, ?>>> ORE_KEY_MAP = new HashMap<>();

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        registerOresDefault(context);
    }


    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(MetallurgyPlus.MODID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }

    private static void registerOresDefault(BootstapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        for (RegistryObject<Block> ore : ModBlocks.ORE_BLOCKS_MAP.values()) {
            ResourceKey<ConfiguredFeature<?, ?>> overworldOreKey = registerKey(ore.getId().getPath());
            ORE_KEY_MAP.put(ore.getId().getPath(), overworldOreKey);
            String baseType = ore.getId().getPath().split("_")[1];
            List<OreConfiguration.TargetBlockState> overworldOres = null;
            if (baseType.equals("stone")) {
                 overworldOres = List.of(
                    OreConfiguration.target(stoneReplaceables, ore.get().defaultBlockState()));
            } else if (baseType.equals("deepslate")) {
                overworldOres = List.of(
                    OreConfiguration.target(deepslateReplaceables, ore.get().defaultBlockState()));
            }
            register(context, overworldOreKey, Feature.ORE, new OreConfiguration(overworldOres, 6));
        }
    }
}