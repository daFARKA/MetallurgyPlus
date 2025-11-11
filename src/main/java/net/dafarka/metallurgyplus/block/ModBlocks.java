package net.dafarka.metallurgyplus.block;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.custom.*;
import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.item.custom.OreBlockItem;
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
    public static final int ENERGY_MAX_RECIEVE = 10000;
    public static final int ENERGY_MAX_EXTRACT = 10000;

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MetallurgyPlus.MODID);
    public static final Map<String, RegistryObject<Block>> MATERIAL_BLOCKS_MAP = new HashMap<>();
    public static final Map<String, RegistryObject<Block>> ORE_BLOCKS_MAP = new HashMap<>();
    public static final Map<String, RegistryObject<Block>> ALLOY_BLOCKS_MAP = new HashMap<>();
    public static final Map<Integer, RegistryObject<CableBlock>> CABLE_BLOCKS_MAP = new HashMap<>();
    public static final Map<Integer, RegistryObject<SolarPanelBlock>> SOLAR_PANEL_BLOCK_MAP = new HashMap<>();
    public static final Map<Integer, RegistryObject<BatteryBlock>> BATTERY_BLOCK_MAP = new HashMap<>();
    public static final Map<String, Integer> MATERIAL_COLOR_MAP = new HashMap<>();
    public static final Map<String, Integer> ORE_COLOR_MAP = new HashMap<>();
    public static final Map<String, Integer> ALLOY_COLOR_MAP = new HashMap<>();
    public static final Map<Integer, Integer> CABLE_COLOR_MAP = new HashMap<>();
    public static final Map<Integer, Integer> SOLAR_PANEL_COLOR_MAP = new HashMap<>();
    public static final Map<Integer, Integer> BATTERY_COLOR_MAP = new HashMap<>();

    public static final Map<String, RegistryObject<Block>> CUSTOM_BLOCKS_MAP = new HashMap<>();

    public static final Map<RegistryObject<Block>, OreRarity> ORE_RARITY_MAP = new HashMap<>();

    public static final RegistryObject<Block> CLAY_MINERAL = registerBlock("clay_mineral",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.CLAY).sound(SoundType.GRAVEL)));

    public static final RegistryObject<Block> ORE_PROCESSING_UNIT = registerBlock("ore_processing_unit",
        () -> new OreProcessingUnitBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Block> ALLOY_SMELTER = registerBlock("alloy_smelter",
        () -> new AlloySmelterBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Block> GRINDER = registerBlock("grinder",
        () -> new GrinderBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Block> POWER_SOURCE = registerBlock("power_source",
        () -> new PowerSourceBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL).noOcclusion()));


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
        RegistryObject<Block> block = registerBlock(name, () -> new Block(BlockBehaviour.Properties.copy(blockBehaviour).sound(soundType)));
        CUSTOM_BLOCKS_MAP.put(name, block);
    }
}
