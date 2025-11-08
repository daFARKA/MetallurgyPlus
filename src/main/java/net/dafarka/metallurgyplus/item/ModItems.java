package net.dafarka.metallurgyplus.item;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.custom.CableBlock;
import net.dafarka.metallurgyplus.block.custom.SolarPanelBlock;
import net.dafarka.metallurgyplus.util.OreRarity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MetallurgyPlus.MODID);

    private static final List<Integer> TIER_COLORS = List.of(0x000000, 0x0000ff, 0x00ff00, 0xff0000, 0xffff00, 0x00ffff, 0xff00ff);

    public static final Map<String, RegistryObject<Item>> MATERIAL_MAP = new HashMap<>();
    public static final Map<String, RegistryObject<Item>> ORE_MAP = new HashMap<>();
    public static final Map<String, RegistryObject<Item>> ALLOY_MAP = new HashMap<>();
    public static final Map<String, Integer> MATERIAL_COLOR_MAP = new HashMap<>();
    public static final Map<String, Integer> ORE_COLOR_MAP = new HashMap<>();
    public static final Map<String, Integer> ALLOY_COLOR_MAP = new HashMap<>();

    public static final RegistryObject<Item> CLAY_MINERAL_RAW = ITEMS.register("clay_mineral_raw", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> KAOLINITE = ITEMS.register("kaolinite", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PLATINUM_LIKE_METALS = ITEMS.register("platinum_like_metals", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SILICON = ITEMS.register("silicon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SULPHUR = ITEMS.register("sulphur", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RARE_EARTH1 = ITEMS.register("rare_earth1", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RARE_EARTH2 = ITEMS.register("rare_earth2", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RARE_EARTH3 = ITEMS.register("rare_earth3", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SMALL_RARE_EARTH = ITEMS.register("small_rare_earth", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STONE_DUST = ITEMS.register("stone_dust", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> LLAMKANA = ITEMS.register("llamkana",
        () -> new ModTools(
            Tiers.NETHERITE,
            new Item.Properties().stacksTo(1).fireResistant().durability(0),
            Component.literal("Llamkana").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD),
            500,
            true
        ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);

        // Base Materials
        registerMaterial("aluminum", 0xb9f0f0);
        registerMaterial("antimony", 0x5465c4);
        registerMaterial("barium", 0x8c8f85);
        registerMaterial("beryllium", 0xb5b5b5);
        registerMaterial("bismuth", 0xbbc4c3);
        registerMaterial("cadmium", 0x56549e);
        registerMaterial("calcium", 0xa3b5b8);
        registerMaterial("cerium", 0x5e7c80);
        registerMaterial("chromium", 0xf5fffb);
        registerMaterial("cobalt", 0x00108a);
        registerMaterial("dysprosium", 0x23825d);
        registerMaterial("erbium", 0x916678);
        registerMaterial("europium", 0x9e8552);
        registerMaterial("gadolinium", 0x0a4d33);
        registerMaterial("gallium", 0x84ada2);
        registerMaterial("graphite", 0x1a1a1a);
        registerMaterial("hafnium", 0x8f8281);
        registerMaterial("holmium", 0x3e635b);
        registerMaterial("indium", 0xb7bdc7);
        registerMaterial("iridium", 0xc4f8ff);
        registerMaterial("lanthanum", 0x232621);
        registerMaterial("lead", 0x534a6b);
        registerMaterial("lithium", 0x9db5c9);
        registerMaterial("lutetium", 0x6c7891);
        registerMaterial("magnesium", 0xe6e8e8);
        registerMaterial("manganese", 0xe3e3d5);
        registerMaterial("molybdenum", 0x7a6f77);
        registerMaterial("neodymium", 0xe3a1c4);
        registerMaterial("nickel", 0xc4ac93);
        registerMaterial("niobium", 0x455075);
        registerMaterial("osmium", 0x457bd9);
        registerMaterial("palladium", 0xdbdbdb);
        registerMaterial("praseodymium", 0x9c9370);
        registerMaterial("promethium", 0xeb3f4e);
        registerMaterial("platinum", 0xc5e6e5);
        registerMaterial("rhenium", 0x5e5e5e);
        registerMaterial("rhodium", 0x362f26);
        registerMaterial("ruthenium", 0xcfc8c6);
        registerMaterial("samarium", 0xf5ffc9);
        registerMaterial("selenium", 0x262625);
        registerMaterial("scandium", 0xf2bcac);
        registerMaterial("silver", 0xcccccc);
        registerMaterial("tantalum", 0xc1a3e3);
        registerMaterial("technetium", 0x808691);
        registerMaterial("terbium", 0xffebfa);
        registerMaterial("thallium", 0x2c2e27);
        registerMaterial("thorium", 0x12151a);
        registerMaterial("thulium", 0x826d6f);
        registerMaterial("tin", 0x9e9e9e);
        registerMaterial("titanium", 0xb1a8ff);
        registerMaterial("tungsten", 0x47474a);
        registerMaterial("uranium", 0x00702d);
        registerMaterial("vanadium", 0x304230);
        registerMaterial("ytterbium", 0x979c6b);
        registerMaterial("yttrium", 0xdbdec1);
        registerMaterial("zinc", 0xc7fff8);
        registerMaterial("zirconium", 0x705a43);

        // Ores
        registerOre("gibbsite", 0x52695a, OreRarity.UNCOMMON);  //Aluminum
        registerOre("bauxite", 0x916b4d, OreRarity.COMMON);   //Aluminum
        registerOre("stibnite", 0x5465c4, OreRarity.UNCOMMON);  //Antimony, Sulphur
        registerOre("beryl", 0xa8bfaf, OreRarity.RARE);     //Beryllium, Aluminum, Silicon
        registerOre("bismuthinite", 0xc7c4b1, OreRarity.RARE);  //Bismuth, Sulphur
        registerOre("chromite", 0x484c59, OreRarity.COMMON);  //Chromium, Iron
        registerOre("cobaltite", 0x091336, OreRarity.RARE); //Cobalt, Sulphur
        registerOre("malachite", 0x064d00, OreRarity.UNCOMMON); //Copper, Graphite
        registerOre("hematite", 0x75614f, OreRarity.COMMON);  //Iron
        registerOre("magnetite", 0x212f36, OreRarity.COMMON); //Iron
        registerOre("limonite", 0xbd6500, OreRarity.COMMON);  //Iron
        registerOre("galena", 0xae9bde, OreRarity.COMMON);    //Lead, Silver, Sulphur
        registerOre("spodumene", 0xeb7cbe, OreRarity.RARE); //Lithium, Aluminum, Silicon
        registerOre("pyrolusite", 0x34ba53, OreRarity.UNCOMMON); //Manganese
        registerOre("molybdenite", 0x5a5275, OreRarity.RARE); //Molybdenum, Rhenium, Sulphur
        registerOre("pentlandite", 0x916f4a, OreRarity.UNCOMMON); //Nickel, Iron, Cobalt, Ruthenium
        registerOre("garnierite", 0x00ff08, OreRarity.RARE); //Nickel, Magnesium
        registerOre("niobite", 0xc0cc62, OreRarity.RARE); //Niobium, Iron, Manganese
        registerOre("sperrylite", 0xe0bad6, OreRarity.VERY_RARE); //Platinum-Like-Metals
        registerOre("gadolinite", 0x751801, OreRarity.VERY_RARE); //Rare Earth 1
        registerOre("monazite", 0xff8800, OreRarity.VERY_RARE); //Rare Earth 2
        registerOre("xenotime", 0x361800, OreRarity.VERY_RARE); //Rare Earth 3
        registerOre("cassiterite", 0x00173b, OreRarity.UNCOMMON); //Tin
        registerOre("ilmenite", 0x332e29, OreRarity.COMMON); //Titanium, Iron
        registerOre("rutile", 0x2e0808, OreRarity.RARE); //Titanium
        registerOre("wolframite", 0x4c7eb0, OreRarity.RARE); //Tungsten, Iron, Manganese
        registerOre("scheelite", 0xdb8348, OreRarity.RARE); //Tungsten, Calcium
        registerOre("patronite", 0x2c2e2a, OreRarity.VERY_RARE); //Vanadium, Sulphur
        registerOre("sphalerite", 0xcfb470, OreRarity.COMMON); //Zinc, Iron, Sulphur
        registerOre("zircon", 0x705a43, OreRarity.RARE); //Zirconium, Silicon, Hafnium
        registerOre("gallite", 0x7e8761, OreRarity.VERY_RARE); //Gallium, Copper, Sulphur
        registerOre("baryte", 0x8fb5c9, OreRarity.UNCOMMON); //Barium, Sulphur
        registerOre("greenockite", 0xd9cc1e, OreRarity.VERY_RARE); //Cadmium, Sulphur
        registerOre("roquesite", 0x619183, OreRarity.VERY_RARE); //Indium, Copper, Sulphur
        registerOre("cooperite", 0xabb4b8, OreRarity.VERY_RARE); //Palladium, Rhodium
        registerOre("thortveitite", 0xe09128, OreRarity.VERY_RARE); //Scandium, Yttrium, Silicon
        registerOre("tantalite", 0x4d2d21, OreRarity.RARE); //Tantalum, Iron, Manganese
        registerOre("crookesite", 0x2b061b, OreRarity.EXTREMELY_RARE); //Thallium, Copper, Silver
        registerOre("uraninite", 0x91b572, OreRarity.RARE); //Uranium, Thorium, Technetium
        registerOre("selenite", 0xebf4fc, OreRarity.UNCOMMON); //Selenium

        // Alloys
        registerAlloy("steel", 0x707070); // Iron, Coal
        registerAlloy("wrought-iron", 0x242020); // Steel, Iron
        registerAlloy("pig-iron", 0xe3ccb1); // Wrought Iron, Sand
        registerAlloy("stainless-steel", 0xe0e0e0); // Steel, Chromium, Nickel, Molybdenum
        registerAlloy("spring-steel", 0xb5b5b5); // Steel, Silicon
        registerAlloy("tungsten-steel", 0xbcccd1); // Steel, Tungsten
        registerAlloy("vanadium-steel", 0x344234); // Steel, Vanadium
        registerAlloy("invar", 0x9c9a84); // Iron, Nickel
        registerAlloy("maraging-steel-1", 0x98bad9); // Steel, Nickel, Cobalt, Molybdenum, Aluminum
        registerAlloy("maraging-steel-2", 0x284866); // Steel, Nickel, Cobalt, Molybdenum, Titanium
        registerAlloy("maraging-steel-3", 0x677d91); // Steel, Nickel, Cobalt, Molybdenum, Niobium
        registerAlloy("manganese-steel", 0x211d24); // Steel, Manganese
        registerAlloy("bronze", 0xd9a84e); // Copper, Tin
        registerAlloy("brass", 0xc9b134); // Copper, Zinc
        registerAlloy("zamak", 0xabd1de); // Aluminum, Zinc
        registerAlloy("aluminum-scandium", 0xd0f1f5); // Aluminum, Scandium
        registerAlloy("aluminum-zirconium", 0xdaf4f7); // Aluminum, Zirconium
        registerAlloy("aluminum-magnesium", 0x5a676b); // Aluminum, Magnesium
        registerAlloy("aluminum-magnesium-zinc", 0x998776); // Aluminum, Magnesium, Zinc
        registerAlloy("nichrome", 0x526275); // Nickel, Chromium
        registerAlloy("cobalt-chromium", 0x010538); // Cobalt, Chromium
        registerAlloy("cupronickel", 0x9c7a59); // Copper, Nickel
        registerAlloy("solder", 0xc8c5ed); // Lead, Tin
        registerAlloy("nitinol", 0x918a9e); // Nickel, Titanium
        registerAlloy("spring-copper", 0xff7e33); // Copper, Beryllium
        registerAlloy("titanium-8al-1mo-1v", 0x1b0933); // Titanium, Aluminum, Molybdenum, Vanadium
        registerAlloy("titanium-6al-2sn-4zr-2mo", 0x1e152b); // Titanium, Aluminum, Tin, Zirconium, Molybdenum
        registerAlloy("titanium-6al-4v", 0x180630); // Titanium, Aluminum, Vanadium
        registerAlloy("titanium-6al-7nb", 0x270f4a); // Titanium, Aluminum, Niobium
        registerAlloy("titanium-10v-2fe-3al", 0x0d021c); // Titanium, Vanadium, Iron, Aluminum

        // Cables
        registerCable(1, TIER_COLORS.get(1));
        registerCable(2, TIER_COLORS.get(2));
        registerCable(3, TIER_COLORS.get(3));
        registerCable(4, TIER_COLORS.get(4));
        registerCable(5, TIER_COLORS.get(5));
        registerCable(6, TIER_COLORS.get(6));

        // Solar Panels
        registerSolarPanel(1, TIER_COLORS.get(1));
        registerSolarPanel(2, TIER_COLORS.get(2));
        registerSolarPanel(3, TIER_COLORS.get(3));
        registerSolarPanel(4, TIER_COLORS.get(4));
        registerSolarPanel(5, TIER_COLORS.get(5));
        registerSolarPanel(6, TIER_COLORS.get(6));
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
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL)));

        ModBlocks.MATERIAL_BLOCKS_MAP.put(name, block);
        ModBlocks.MATERIAL_COLOR_MAP.put(name, color);
    }

    public static final String[] ORE_COMPONENT_NAMES = {"raw", "dust"};
    public static final String[] ORE_BASE_NAME = {"stone", "deepslate"};
    private static void registerOre(String oreName, int color, OreRarity oreRarity) {
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
                () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE).sound(SoundType.STONE)));

            ModBlocks.ORE_BLOCKS_MAP.put(name, block);
            ModBlocks.ORE_COLOR_MAP.put(name, color);
            ModBlocks.ORE_RARITY_MAP.put(block, oreRarity);
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
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL)));

        ModBlocks.ALLOY_BLOCKS_MAP.put(name, block);
        ModBlocks.ALLOY_COLOR_MAP.put(name, color);
    }

    private static void registerCable(int tier, int color) {
        String name = "cable" + tier + "_block";
        RegistryObject<CableBlock> block = ModBlocks.registerBlock(name,
            () -> new CableBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.WOOL), tier));
        ModBlocks.CABLE_BLOCKS_MAP.put(tier, block);
        ModBlocks.CABLE_COLOR_MAP.put(tier, color);
    }

    private static void registerSolarPanel(int tier, int color) {
        String name = "solar_panel" + tier + "_block";
        RegistryObject<SolarPanelBlock> block = ModBlocks.registerBlock(name,
            () -> new SolarPanelBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL), tier));
        ModBlocks.SOLAR_PANEL_BLOCK_MAP.put(tier, block);
        ModBlocks.SOLAR_PANEL_COLOR_MAP.put(tier, color);
    }
}
