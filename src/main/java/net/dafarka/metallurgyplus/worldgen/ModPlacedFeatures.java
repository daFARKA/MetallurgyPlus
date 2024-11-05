package net.dafarka.metallurgyplus.worldgen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModPlacedFeatures {
    public static final Map<String, ResourceKey<PlacedFeature>> ORE_PLACED_MAP = new HashMap<>();

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        registerOresDefault(context);
    }


    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(MetallurgyPlus.MODID, name));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static void registerOresDefault(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        for (RegistryObject<Block> ore : ModBlocks.ORE_BLOCKS_MAP.values()) {
            ResourceKey<PlacedFeature> orePlacedKey = registerKey(ore.getId().getPath() + "_placed");
            ORE_PLACED_MAP.put(ore.getId().getPath(), orePlacedKey);

            switch (ModBlocks.ORE_RARITY_MAP.get(ore)) {
                case COMMON -> register(context, orePlacedKey, configuredFeatures.getOrThrow(ModConfiguredFeatures.ORE_KEY_MAP.get(ore.getId().getPath())),
                    ModOrePlacement.commonOrePlacement(10, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));
                case UNCOMMON -> register(context, orePlacedKey, configuredFeatures.getOrThrow(ModConfiguredFeatures.ORE_KEY_MAP.get(ore.getId().getPath())),
                    ModOrePlacement.commonOrePlacement(5, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(50))));
                case RARE -> register(context, orePlacedKey, configuredFeatures.getOrThrow(ModConfiguredFeatures.ORE_KEY_MAP.get(ore.getId().getPath())),
                    ModOrePlacement.commonOrePlacement(1, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(20))));
                case VERY_RARE -> register(context, orePlacedKey, configuredFeatures.getOrThrow(ModConfiguredFeatures.ORE_KEY_MAP.get(ore.getId().getPath())),
                    ModOrePlacement.rareOrePlacement(2, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(-10))));
                case EXTREMELY_RARE -> register(context, orePlacedKey, configuredFeatures.getOrThrow(ModConfiguredFeatures.ORE_KEY_MAP.get(ore.getId().getPath())),
                    ModOrePlacement.rareOrePlacement(5, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(-30))));
            }

        }
    }
}
