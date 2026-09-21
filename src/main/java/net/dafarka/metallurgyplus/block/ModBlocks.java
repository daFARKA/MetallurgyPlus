package net.dafarka.metallurgyplus.block;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.custom.*;
import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.item.custom.OreBlockItem;
import net.dafarka.metallurgyplus.recipe.ModRecipeTypes;
import net.dafarka.metallurgyplus.util.OreRarity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModBlocks {
    public static final int ENERGY_CONSUMPTION_PER_TICK = 50;
    public static final int ENERGY_CAPACITY = 100000;
    public static final int ENERGY_MAX_RECEIVE = 10000;
    public static final int ENERGY_MAX_EXTRACT = 10000;

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MetallurgyPlus.MODID);
    public static final Map<String, RegistryObject<Block>> MATERIAL_BLOCKS_MAP = new HashMap<>();
    public static final Map<String, RegistryObject<Block>> ORE_BLOCKS_MAP = new HashMap<>();
    public static final Map<String, RegistryObject<Block>> ALLOY_BLOCKS_MAP = new HashMap<>();
    public static final Map<String, RegistryObject<Block>> GEM_BLOCKS_MAP = new HashMap<>();
    public static final Map<Integer, RegistryObject<CableBlock>> CABLE_BLOCKS_MAP = new HashMap<>();
    public static final Map<Integer, RegistryObject<BatteryBlock>> BATTERY_BLOCK_MAP = new HashMap<>();
    public static final Map<Integer, RegistryObject<SolarPanelBlock>> SOLAR_PANEL_BLOCK_MAP = new HashMap<>();
    public static final Map<String, Integer> MATERIAL_COLOR_MAP = new HashMap<>();
    public static final Map<String, Integer> ORE_COLOR_MAP = new HashMap<>();
    public static final Map<String, Integer> ALLOY_COLOR_MAP = new HashMap<>();
    public static final Map<String, Integer> GEM_COLOR_MAP = new HashMap<>();
    public static final Map<Integer, Integer> CABLE_COLOR_MAP = new HashMap<>();
    public static final Map<Integer, Integer> BATTERY_COLOR_MAP = new HashMap<>();
    public static final Map<Integer, Integer> SOLAR_PANEL_COLOR_MAP = new HashMap<>();

    public static final Map<String, RegistryObject<Block>> CUSTOM_BLOCKS_MAP = new HashMap<>();

    public static final Map<RegistryObject<Block>, OreRarity> ORE_RARITY_MAP = new HashMap<>();

    public static final RegistryObject<Block> CLAY_MINERAL = registerBlock("clay_mineral",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.CLAY).sound(SoundType.GRAVEL)));

    public static final int[][] ORE_PROCESSING_UNIT_INPUT_POSITION = {{8, 39}};
    public static final int[][] ORE_PROCESSING_UNIT_OUTPUT_POSITIONS = {{62, 21}, {80, 21}, {98, 21}, {116, 21}, {134, 21}, {152, 21},
        {62, 39}, {80, 39}, {98, 39}, {116, 39}, {134, 39}, {152, 39}, {62, 57}, {80, 57}, {98, 57}, {116, 57}, {134, 57}, {152, 57}};
    public static final RegistryObject<MachineBlock> ORE_PROCESSING_UNIT = registerBlock("ore_processing_unit",
        () -> new MachineBlock(
            BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL).noOcclusion(),
            ModRecipeTypes.ORE_PROCESSING_UNIT_TYPE,
            ORE_PROCESSING_UNIT_INPUT_POSITION,
            ORE_PROCESSING_UNIT_OUTPUT_POSITIONS,
            new int[]{31, 43}
        ));

    public static final int[][] ALLOY_SMELTER_INPUT_POSITIONS = {{8, 18}, {26, 18}, {8, 36}, {26, 36}, {50, 18}, {68, 18}, {50, 36}, {68, 36}};
    public static final int[][] ALLOY_SMELTER_OUTPUT_POSITIONS = {{116, 18}, {134, 18}, {152, 18}, {116, 36}, {134, 36}, {152, 36}, {116, 54}, {134, 54}, {152, 54}};
    public static final RegistryObject<MachineBlock> ALLOY_SMELTER = registerBlock("alloy_smelter",
        () -> new MachineBlock(
            BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL).noOcclusion(),
            ModRecipeTypes.ALLOY_SMELTER_TYPE,
            ALLOY_SMELTER_INPUT_POSITIONS,
            ALLOY_SMELTER_OUTPUT_POSITIONS,
            new int[]{87, 31}
        ));

    public static final int[][] GRINDER_INPUT_POSITIONS = {{8, 21}, {26, 21}, {8, 39}, {26, 39}};
    public static final int[][] GRINDER_OUTPUT_POSITIONS = {{80, 21}, {98, 21}, {116, 21}, {134, 21}, {152, 21}, {80, 39}, {98, 39}, {116, 39}, {134, 39}, {152, 39},
        {80, 57}, {98, 57}, {116, 57}, {134, 57}, {152, 57}};
    public static final RegistryObject<MachineBlock> GRINDER = registerBlock("grinder",
        () -> new MachineBlock(
            BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL).noOcclusion(),
            ModRecipeTypes.GRINDER_TYPE,
            GRINDER_INPUT_POSITIONS,
            GRINDER_OUTPUT_POSITIONS,
            new int[]{48, 34}
        ));

    public static final int[][] PRESS_INPUT_POSITION = {{40, 35}};
    public static final int[][] PRESS_OUTPUT_POSITIONS = {{98, 25}, {116, 25}, {98, 43}, {116, 43}};
    public static final RegistryObject<MachineBlock> PRESS = registerBlock("press",
        () -> new MachineBlock(
            BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL).noOcclusion(),
            ModRecipeTypes.PRESS_TYPE,
            PRESS_INPUT_POSITION,
            PRESS_OUTPUT_POSITIONS,
            new int[]{64, 38}
        ));

    public static final int[][] EXTRACTOR_INPUT_POSITIONS = {{22, 21}, {40, 21}, {22, 39}, {40, 39}};
    public static final int[][] EXTRACTOR_OUTPUT_POSITIONS = {{98, 21}, {116, 21}, {134, 21}, {98, 39}, {116, 39}, {134, 39}, {98, 57}, {116, 57}, {134, 57}};
    public static final RegistryObject<MachineBlock> EXTRACTOR = registerBlock("extractor",
        () -> new MachineBlock(
            BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL).noOcclusion(),
            ModRecipeTypes.EXTRACTOR_TYPE,
            EXTRACTOR_INPUT_POSITIONS,
            EXTRACTOR_OUTPUT_POSITIONS,
            new int[]{64, 34}
        ));

    public static final int[][] GEMSTONE_CUTTER_INPUT_POSITION = {{40, 35}};
    public static final int[][] GEMSTONE_CUTTER_OUTPUT_POSITIONS = {{98, 17}, {116, 17}, {134, 17}, {98, 35}, {116, 35}, {134, 35}, {98, 53}, {116, 53}, {134, 53}};
    public static final RegistryObject<MachineBlock> GEMSTONE_CUTTER = registerBlock("gemstone_cutter",
        () -> new MachineBlock(
            BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL).noOcclusion(),
            ModRecipeTypes.GEMSTONE_CUTTER_TYPE,
            GEMSTONE_CUTTER_INPUT_POSITION,
            GEMSTONE_CUTTER_OUTPUT_POSITIONS,
            new int[]{64, 38}
        ));

    public static final RegistryObject<Block> QUARRY = registerBlock("quarry",
        () -> new QuarryBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Block> POWER_SOURCE = registerBlock("power_source",
        () -> new PowerSourceBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Block> SACK_STATION = registerBlock("sack_station",
        () -> new SackStationBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL).noOcclusion()
            .isRedstoneConductor((state, level, pos) -> false)));


    public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static <T extends Block> RegistryObject<T> registerOreBlock(String name, Supplier<T> block, OreRarity rarity) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerOreBlockItem(name, toReturn, rarity);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerOreBlockItem(String name, RegistryObject<T> block, OreRarity rarity) {
        return ModItems.ITEMS.register(name, () -> new OreBlockItem(block.get(), new Item.Properties(), rarity));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);

        // Custom Blocks
        registerCustomBlock("machine_frame", Blocks.IRON_BLOCK, SoundType.METAL);
    }

    private static void registerCustomBlock(String name, BlockBehaviour blockBehaviour, SoundType soundType) {
        RegistryObject<Block> block = registerBlock(name, () -> new Block(BlockBehaviour.Properties.copy(blockBehaviour).sound(soundType).noOcclusion()));
        CUSTOM_BLOCKS_MAP.put(name, block);
    }
}
