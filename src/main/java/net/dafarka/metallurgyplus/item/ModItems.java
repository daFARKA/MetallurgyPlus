package net.dafarka.metallurgyplus.item;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
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

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MetallurgyPlus.MODID);
    public static final Map<String, RegistryObject<Item>> MATERIAL_MAP = new HashMap<>();
    public static final Map<String, RegistryObject<Item>> ORE_MAP = new HashMap<>();
    public static final Map<String, RegistryObject<Item>> ALLOY_MAP = new HashMap<>();
    public static final Map<String, Integer> MATERIAL_COLOR_MAP = new HashMap<>();
    public static final Map<String, Integer> ORE_COLOR_MAP = new HashMap<>();
    public static final Map<String, Integer> ALLOY_COLOR_MAP = new HashMap<>();

    public static final RegistryObject<Item> CLAY_MINERAL_RAW = ITEMS.register("clay_mineral_raw", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> KAOLINITE = ITEMS.register("kaolinite", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SILICON = ITEMS.register("silicon", () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);

        // Register Base Materials
        registerMaterial("aluminum", 0xb9f0f0);
        registerMaterial("antimony", 0x5465c4);
        registerMaterial("barium", 0x8c8f85);
        registerMaterial("beryllium", 0xb5b5b5);
        registerMaterial("cadmium", 0x56549e);
        registerMaterial("chromium", 0xf5fffb);
        registerMaterial("cobalt", 0x00108a);
        registerMaterial("gallium", 0x84ada2);
        registerMaterial("hafnium", 0x8f8281);
        registerMaterial("indium", 0xb7bdc7);
        registerMaterial("iridium", 0xc4f8ff);
        registerMaterial("lead", 0x534a6b);
        registerMaterial("magnesium", 0xe6e8e8);
        registerMaterial("manganese", 0xe3e3d5);
        registerMaterial("molybdenum", 0x7a6f77);
        registerMaterial("nickel", 0xc4ac93);
        registerMaterial("niobium", 0x455075);
        registerMaterial("osmium", 0x457bd9);
        registerMaterial("palladium", 0xdbdbdb);
        registerMaterial("platinum", 0xc5e6e5);
        registerMaterial("rhenium", 0x5e5e5e);
        registerMaterial("rhodium", 0x362f26);
        registerMaterial("ruthenium", 0xcfc8c6);
        registerMaterial("scandium", 0xf2bcac);
        registerMaterial("silver", 0xcccccc);
        registerMaterial("tantalum", 0xc1a3e3);
        registerMaterial("technetium", 0x808691);
        registerMaterial("thallium", 0x2c2e27);
        registerMaterial("tin", 0x9e9e9e);
        registerMaterial("titanium", 0xb1a8ff);
        registerMaterial("tungsten", 0x47474a);
        registerMaterial("vanadium", 0x304230);
        registerMaterial("yttrium", 0xdbdec1);
        registerMaterial("zinc", 0xc7fff8);
        registerMaterial("zirconium", 0x705a43);

        // Register Ores
        registerOre("gibbsite", 0x52695a);
        registerOre("bauxite", 0x916b4d);

        // Register Alloys
        registerAlloy("steel", 0x707070);
    }

    public static final String[] MATERIAL_COMPONENT_NAMES = {"ingot", "dust", "gear", "nugget", "plate", "rod", "raw"};
    private static void registerMaterial(String materialName, int color) {
        for (String component : MATERIAL_COMPONENT_NAMES) {
            String name = materialName + "_" + component;
            RegistryObject<Item> item = ITEMS.register(name, () ->
                new Item(new Item.Properties())
            );
            MATERIAL_MAP.put(name, item);
            MATERIAL_COLOR_MAP.put(name, color);
        }
        String name = materialName + "_block";
        RegistryObject<Block> block = ModBlocks.registerBlock(name,
            () -> new net.minecraft.world.level.block.Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL)));

        ModBlocks.MATERIAL_BLOCKS_MAP.put(name, block);
        ModBlocks.MATERIAL_COLOR_MAP.put(name, color);
    }

    public static final String[] ORE_COMPONENT_NAMES = {"raw", "dust"};
    public static final String[] ORE_BASE_NAME = {"stone", "deepslate"};
    private static void registerOre(String oreName, int color) {
        for (String component : ORE_COMPONENT_NAMES) {
            String name = oreName + "_" + component;
            RegistryObject<Item> item = ITEMS.register(name, () ->
                new Item(new Item.Properties())
            );
            ORE_MAP.put(name, item);
            ORE_COLOR_MAP.put(name, color);
        }

        for (String base : ORE_BASE_NAME) {
            String name = oreName + "_" + base + "_block";
            RegistryObject<Block> block = ModBlocks.registerBlock(name,
                () -> new net.minecraft.world.level.block.Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE).sound(SoundType.STONE)));

            ModBlocks.ORE_BLOCKS_MAP.put(name, block);
            ModBlocks.ORE_COLOR_MAP.put(name, color);
        }
    }

    public static final String[] ALLOY_COMPONENT_NAMES = {"ingot", "gear", "nugget", "plate", "rod"};
    private static void registerAlloy(String materialName, int color) {
        for (String component : ALLOY_COMPONENT_NAMES) {
            String name = materialName + "_" + component;
            RegistryObject<Item> item = ITEMS.register(name, () ->
                new Item(new Item.Properties())
            );
            ALLOY_MAP.put(name, item);
            ALLOY_COLOR_MAP.put(name, color);
        }
        String name = materialName + "_block";
        RegistryObject<Block> block = ModBlocks.registerBlock(name,
            () -> new net.minecraft.world.level.block.Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL)));

        ModBlocks.ALLOY_BLOCKS_MAP.put(name, block);
        ModBlocks.ALLOY_COLOR_MAP.put(name, color);
    }
}
