package net.dafarka.metallurgyplus.item;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.custom.BatteryBlock;
import net.dafarka.metallurgyplus.block.custom.BatteryCellBlock;
import net.dafarka.metallurgyplus.block.custom.CableBlock;
import net.dafarka.metallurgyplus.block.custom.SolarPanelBlock;
import net.dafarka.metallurgyplus.block.entity.ModBlockEntities;
import net.dafarka.metallurgyplus.block.entity.multiblock.MBControllerBlockEntity;
import net.dafarka.metallurgyplus.block.multiblock.MBControllerBlock;
import net.dafarka.metallurgyplus.block.multiblock.MBEnergyPortBlock;
import net.dafarka.metallurgyplus.block.multiblock.MBFluidPortBlock;
import net.dafarka.metallurgyplus.block.multiblock.MBItemPortBlock;
import net.dafarka.metallurgyplus.item.sack.SackItem;
import net.dafarka.metallurgyplus.item.tool.ModTools;
import net.dafarka.metallurgyplus.item.tool.ModularPickaxeItem;
import net.dafarka.metallurgyplus.item.tool.ModularToolPartItem;
import net.dafarka.metallurgyplus.item.tool.ModularToolStats;
import net.dafarka.metallurgyplus.util.OreRarity;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MetallurgyPlus.MODID);
    private static final ModularToolStats DEFAULT_TOOL_STATS = new ModularToolStats(100, 2.0F, 1.2F);

    private static final List<Integer> TIER_COLORS = List.of(
        0x000000, // Tier 0: Base / Neutral

        // 1. B (Pure Blue: Pale Sky -> Saturated Pure Blue)
        0xdbeafe, 0x93c5fd, 0x60a5fa, 0x3b82f6, 0x1d4ed8, 0x0026ff,

        // 2. G (Pure Green: Pale Minty-Green -> Vivid Pure Green)
        0xdcfce7, 0x86efac, 0x4ade80, 0x22c55e, 0x16a34a, 0x00c814,

        // 3. R (Pure Red: Pale Pinkish-Red -> Vivid Pure Red)
        0xffe4e6, 0xfca5a5, 0xf87171, 0xef4444, 0xdc2626, 0xff0000,

        // 4. BG (Balanced Blue-Green / Cyan: Pale Aqua -> Vivid Cyan)
        0xcffafe, 0x67e8f9, 0x22d3ee, 0x06b6d4, 0x00bfff, 0x00e1e1,

        // 5. Bg (Blue dominant, minor green / Deep Sky: Pale Ice -> Saturated Azure)
        0xdbeafe, 0x7dd3fc, 0x38bdf8, 0x0ea5e9, 0x0284c7, 0x0066ff,

        // 6. Gb (Green dominant, minor blue / Teal: Pale Seafoam -> Vivid Teal)
        0xccfbf1, 0x5eead4, 0x2dd4bf, 0x14b8a6, 0x0d9488, 0x00b386,

        // 7. GR (Balanced Green-Red / Yellow: Pale Cream -> Pure Vivid Yellow)
        0xfef9c3, 0xfef08a, 0xfde047, 0xfacc15, 0xffeb3b, 0xffff00,

        // 8. Gr (Green dominant, minor red / Lime-Chartreuse: Soft Lime -> Vibrant Lime)
        0xecfccb, 0xbef264, 0xa3e635, 0x84cc16, 0x65a30d, 0x66cc00,

        // 9. Rg (Red dominant, minor green / Orange -> Amber Brown: Soft Peach -> Rich Amber Brown)
        0xffedd5, 0xfba368, 0xf97316, 0xe05615, 0xbf4000, 0x8f3000,

        // 10. BR (Balanced Blue-Red / Magenta: Pale Lavender-Pink -> Pure Magenta)
        0xfae8ff, 0xf0abfc, 0xe879f9, 0xd946ef, 0xc026d3, 0xff00ff,

        // 11. Br (Blue dominant, minor red / Electric Violet: Soft Periwinkle -> Vivid Violet)
        0xe0e7ff, 0xa5b4fc, 0x818cf8, 0x6366f1, 0x4f46e5, 0x6a00ff,

        // 12. Rb (Red dominant, minor blue / Crimson-Rose: Pale Blush -> Deep Radiant Crimson)
        0xfce7f3, 0xf472b6, 0xf43f5e, 0xe11d48, 0xbe123c, 0xd00048
    );

    private static final List<Integer> MAJOR_TIER_COLORS = List.of(
        TIER_COLORS.get(0),  // Base / Neutral (0x000000)
        TIER_COLORS.get(6),  // 1. B  peak (Deep Cobalt)
        TIER_COLORS.get(12), // 2. G  peak (Deep Forest Green)
        TIER_COLORS.get(18), // 3. R  peak (Deep Crimson)
        TIER_COLORS.get(24), // 4. BG peak (Deep Cyan)
        TIER_COLORS.get(30), // 5. Bg peak (Midnight Ocean)
        TIER_COLORS.get(36), // 6. Gb peak (Dark Pine/Teal)
        TIER_COLORS.get(42), // 7. GR peak (Pure Saturated Yellow)
        TIER_COLORS.get(48), // 8. Gr peak (Deep Olive Green)
        TIER_COLORS.get(54), // 9. Rg peak (Deep Warm Brown)
        TIER_COLORS.get(60), // 10. BR peak (Deep Rich Magenta)
        TIER_COLORS.get(66), // 11. Br peak (Deep Midnight Indigo)
        TIER_COLORS.get(72)  // 12. Rb peak (Deep Bordeaux/Wine)
    );

    public static final Map<String, RegistryObject<? extends Item>> CUSTOM_ITEM_MAP = new HashMap<>();

    public static final Map<String, RegistryObject<Item>> MATERIAL_MAP = new HashMap<>();
    public static final Map<String, RegistryObject<Item>> ORE_MAP = new HashMap<>();
    public static final Map<String, RegistryObject<Item>> ALLOY_MAP = new HashMap<>();
    public static final Map<String, RegistryObject<Item>> VANILLA_MAP = new HashMap<>();
    public static final Map<String, RegistryObject<Item>> GEM_MAP = new HashMap<>();
    public static final Map<String, RegistryObject<Item>> FISSION_MAP = new HashMap<>();
    public static final Map<Integer, RegistryObject<Item>> COIL_MAP = new HashMap<>();
    public static final Map<Integer, RegistryObject<Item>> SACK_MAP = new HashMap<>();

    public static final Map<String, Integer> MATERIAL_COLOR_MAP = new HashMap<>();
    public static final Map<String, Integer> ORE_COLOR_MAP = new HashMap<>();
    public static final Map<String, Integer> ALLOY_COLOR_MAP = new HashMap<>();
    public static final Map<String, Integer> VANILLA_COLOR_MAP = new HashMap<>();
    public static final Map<String, Integer> GEM_COLOR_MAP = new HashMap<>();
    public static final Map<String, Integer> FISSION_COLOR_MAP = new HashMap<>();
    public static final Map<Integer, Integer> COIL_COLOR_MAP = new HashMap<>();
    public static final Map<Integer, Integer> SACK_COLOR_MAP = new HashMap<>();

    public static final List<String> FISSION_TIER_LIST = new ArrayList<>();

    public static final RegistryObject<Item> LLAMKANA = ITEMS.register("llamkana",
        () -> new ModTools(
            Tiers.NETHERITE,
            new Item.Properties().stacksTo(1).fireResistant().durability(0),
            Component.translatable("item.metallurgyplus.llamkana").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD),
            500,
            true
        ));

    public static final RegistryObject<ModularPickaxeItem> MODULAR_PICKAXE = ITEMS.register("modular_pickaxe",
        () -> new ModularPickaxeItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);

        // Custom Items
        registerCustomItem("silicon");
        registerCustomItem("clay_mineral_raw");
        registerCustomItem("kaolinite");
        registerCustomItem("sulphur");
        registerCustomItem("rare_earth_1");
        registerCustomItem("rare_earth_2");
        registerCustomItem("rare_earth_3");
        registerCustomItem("small_rare_earth");
        registerCustomItem("stone_dust");
        registerCustomItem("magma_dust");
        registerCustomItem("saw_dust");
        registerCustomItem("quartz_dust");
        registerCustomItem("energy_core");
        registerCustomItem("rubber");
        registerCustomItem("tree_sap");
        registerCustomItem("mixed_rare_earth_alloy");
        registerCustomItem("mixed_rare_earth_alloy_plate");
        registerCustomItem("solar_cell");
        registerCustomItem("mercury");
        registerCustomItem("fertilizer", BoneMealItem::new);

        // Base Materials
        registerMaterial("aluminum", 0xb9f0f0, DEFAULT_TOOL_STATS);
        registerMaterial("antimony", 0x5465c4, DEFAULT_TOOL_STATS);
        registerMaterial("barium", 0x8c8f85, DEFAULT_TOOL_STATS);
        registerMaterial("beryllium", 0xb5b5b5, DEFAULT_TOOL_STATS);
        registerMaterial("bismuth", 0xbbc4c3, DEFAULT_TOOL_STATS);
        registerMaterial("boron", 0xd9d9d9, DEFAULT_TOOL_STATS);
        registerMaterial("cadmium", 0x56549e, DEFAULT_TOOL_STATS);
        registerMaterial("calcium", 0xa3b5b8, DEFAULT_TOOL_STATS);
        registerMaterial("cerium", 0x5e7c80, DEFAULT_TOOL_STATS);
        registerMaterial("chromium", 0xf5fffb, DEFAULT_TOOL_STATS);
        registerMaterial("cobalt", 0x00108a, DEFAULT_TOOL_STATS);
        registerMaterial("dysprosium", 0x23825d, DEFAULT_TOOL_STATS);
        registerMaterial("erbium", 0x916678, DEFAULT_TOOL_STATS);
        registerMaterial("europium", 0x9e8552, DEFAULT_TOOL_STATS);
        registerMaterial("gadolinium", 0x0a4d33, DEFAULT_TOOL_STATS);
        registerMaterial("gallium", 0x84ada2, DEFAULT_TOOL_STATS);
        registerMaterial("germanium", 0xc9c7ad, DEFAULT_TOOL_STATS);
        registerMaterial("graphite", 0x1a1a1a, DEFAULT_TOOL_STATS);
        registerMaterial("hafnium", 0x8f8281, DEFAULT_TOOL_STATS);
        registerMaterial("holmium", 0x3e635b, DEFAULT_TOOL_STATS);
        registerMaterial("indium", 0xb7bdc7, DEFAULT_TOOL_STATS);
        registerMaterial("iridium", 0xc4f8ff, DEFAULT_TOOL_STATS);
        registerMaterial("lanthanum", 0x232621, DEFAULT_TOOL_STATS);
        registerMaterial("lead", 0x534a6b, DEFAULT_TOOL_STATS);
        registerMaterial("lithium", 0x9db5c9, DEFAULT_TOOL_STATS);
        registerMaterial("lutetium", 0x6c7891, DEFAULT_TOOL_STATS);
        registerMaterial("magnesium", 0xe6e8e8, DEFAULT_TOOL_STATS);
        registerMaterial("manganese", 0xe3e3d5, DEFAULT_TOOL_STATS);
        registerMaterial("molybdenum", 0x7a6f77, DEFAULT_TOOL_STATS);
        registerMaterial("neodymium", 0xe3a1c4, DEFAULT_TOOL_STATS);
        registerMaterial("nickel", 0xc4ac93, DEFAULT_TOOL_STATS);
        registerMaterial("niobium", 0x455075, DEFAULT_TOOL_STATS);
        registerMaterial("osmium", 0x457bd9, DEFAULT_TOOL_STATS);
        registerMaterial("palladium", 0xdbdbdb, DEFAULT_TOOL_STATS);
        registerMaterial("praseodymium", 0x9c9370, DEFAULT_TOOL_STATS);
        registerMaterial("promethium", 0xeb3f4e, DEFAULT_TOOL_STATS);
        registerMaterial("platinum", 0xc5e6e5, DEFAULT_TOOL_STATS);
        registerMaterial("rhenium", 0x5e5e5e, DEFAULT_TOOL_STATS);
        registerMaterial("rhodium", 0x362f26, DEFAULT_TOOL_STATS);
        registerMaterial("ruthenium", 0xcfc8c6, DEFAULT_TOOL_STATS);
        registerMaterial("samarium", 0xf5ffc9, DEFAULT_TOOL_STATS);
        registerMaterial("selenium", 0x262625, DEFAULT_TOOL_STATS);
        registerMaterial("scandium", 0xf2bcac, DEFAULT_TOOL_STATS);
        registerMaterial("silver", 0xcccccc, DEFAULT_TOOL_STATS);
        registerMaterial("tantalum", 0xc1a3e3, DEFAULT_TOOL_STATS);
        registerMaterial("technetium", 0x808691, DEFAULT_TOOL_STATS);
        registerMaterial("tellurium", 0xa0cdfa, DEFAULT_TOOL_STATS);
        registerMaterial("terbium", 0xffebfa, DEFAULT_TOOL_STATS);
        registerMaterial("thallium", 0x2c2e27, DEFAULT_TOOL_STATS);
        registerMaterial("thorium", 0x12151a, DEFAULT_TOOL_STATS);
        registerMaterial("thulium", 0x826d6f, DEFAULT_TOOL_STATS);
        registerMaterial("tin", 0x9e9e9e, DEFAULT_TOOL_STATS);
        registerMaterial("titanium", 0xb1a8ff, DEFAULT_TOOL_STATS);
        registerMaterial("tungsten", 0x47474a, DEFAULT_TOOL_STATS);
        registerMaterial("uranium", 0x00702d, DEFAULT_TOOL_STATS);
        registerMaterial("vanadium", 0x304230, DEFAULT_TOOL_STATS);
        registerMaterial("ytterbium", 0x979c6b, DEFAULT_TOOL_STATS);
        registerMaterial("yttrium", 0xdbdec1, DEFAULT_TOOL_STATS);
        registerMaterial("zinc", 0xc7fff8, DEFAULT_TOOL_STATS);
        registerMaterial("zirconium", 0x705a43, DEFAULT_TOOL_STATS);
        registerMaterial("arsenic", 0x6b7075, DEFAULT_TOOL_STATS);

        // Ores
        registerOre("gibbsite", 0x52695a, OreRarity.UNCOMMON);  // Aluminum
        registerOre("bauxite", 0x916b4d, OreRarity.COMMON);   // Aluminum
        registerOre("stibnite", 0x5465c4, OreRarity.UNCOMMON);  // Antimony, Sulphur
        registerOre("beryl", 0xa8bfaf, OreRarity.RARE);     // Beryllium, Aluminum, Silicon - [Aquamarine, Emerald, Morganite, Red-Beryl]
        registerOre("bismuthinite", 0xc7c4b1, OreRarity.RARE);  // Bismuth, Sulphur
        registerOre("chromite", 0x484c59, OreRarity.COMMON);  // Chromium, Iron
        registerOre("cobaltite", 0x091336, OreRarity.RARE); // Cobalt, Sulphur
        registerOre("malachite", 0x064d00, OreRarity.UNCOMMON); // Copper - [Malachite]
        registerOre("hematite", 0x75614f, OreRarity.COMMON);  // Iron
        registerOre("magnetite", 0x212f36, OreRarity.COMMON); // Iron
        registerOre("limonite", 0xbd6500, OreRarity.COMMON);  // Iron
        registerOre("galena", 0xae9bde, OreRarity.COMMON);    // Lead, Silver, Sulphur
        registerOre("spodumene", 0xeb7cbe, OreRarity.RARE); // Lithium, Aluminum, Silicon - [Hiddenite]
        registerOre("pyrolusite", 0x34ba53, OreRarity.UNCOMMON); // Manganese
        registerOre("molybdenite", 0x5a5275, OreRarity.RARE); // Molybdenum, Rhenium, Sulphur
        registerOre("pentlandite", 0x916f4a, OreRarity.UNCOMMON); // Nickel, Iron, Cobalt, Ruthenium
        registerOre("garnierite", 0x00ff08, OreRarity.RARE); // Nickel, Magnesium
        registerOre("niobite", 0xc0cc62, OreRarity.RARE); // Niobium, Iron, Manganese
        registerOre("sperrylite", 0xe0bad6, OreRarity.VERY_RARE); // Platinum-Like-Metals
        registerOre("gadolinite", 0x751801, OreRarity.VERY_RARE); // Rare Earth 1
        registerOre("monazite", 0xff8800, OreRarity.VERY_RARE); // Rare Earth 2
        registerOre("xenotime", 0x361800, OreRarity.VERY_RARE); // Rare Earth 3
        registerOre("cassiterite", 0x00173b, OreRarity.UNCOMMON); // Tin
        registerOre("ilmenite", 0x332e29, OreRarity.COMMON); // Titanium, Iron
        registerOre("rutile", 0x2e0808, OreRarity.RARE); // Titanium
        registerOre("wolframite", 0x4c7eb0, OreRarity.RARE); // Tungsten, Iron, Manganese
        registerOre("scheelite", 0xdb8348, OreRarity.RARE); // Tungsten, Calcium
        registerOre("patronite", 0x2c2e2a, OreRarity.VERY_RARE); // Vanadium, Sulphur
        registerOre("sphalerite", 0xcfb470, OreRarity.COMMON); // Zinc, Iron, Sulphur
        registerOre("zircon", 0x705a43, OreRarity.RARE); // Zirconium, Silicon, Hafnium - [Zircon]
        registerOre("gallite", 0x7e8761, OreRarity.VERY_RARE); // Gallium, Copper, Sulphur
        registerOre("baryte", 0x8fb5c9, OreRarity.UNCOMMON); // Barium, Sulphur
        registerOre("greenockite", 0xd9cc1e, OreRarity.VERY_RARE); // Cadmium, Sulphur
        registerOre("roquesite", 0x619183, OreRarity.VERY_RARE); // Indium, Copper, Sulphur
        registerOre("cooperite", 0xabb4b8, OreRarity.VERY_RARE); // Palladium, Rhodium
        registerOre("thortveitite", 0xe09128, OreRarity.VERY_RARE); // Scandium, Yttrium, Silicon
        registerOre("tantalite", 0x4d2d21, OreRarity.RARE); // Tantalum, Iron, Manganese
        registerOre("crookesite", 0x2b061b, OreRarity.EXTREMELY_RARE); // Thallium, Copper, Silver
        registerOre("uraninite", 0x91b572, OreRarity.RARE); // Uranium, Thorium, Technetium
        registerOre("selenite", 0xebf4fc, OreRarity.UNCOMMON); // Selenium
        registerOre("feldspar", 0xc77b63, OreRarity.COMMON); // Aluminum, Silicon - [Amazonite, Moonstone, Sunstone]
        registerOre("borax", 0xffffff, OreRarity.UNCOMMON); // Boron
        registerOre("germanite", 0x80725d, OreRarity.RARE); // Copper, Germanium, Iron, Sulphur
        registerOre("sylvanite", 0xfff7bd, OreRarity.VERY_RARE); // Silver, Gold, Tellurium
        registerOre("calaverite", 0xc7bd71, OreRarity.UNCOMMON); // Gold, Tellurium
        registerOre("corundum", 0xcf95db, OreRarity.RARE); // [Ruby, Sapphire, Lazulite]
        registerOre("apatite", 0x82fff9, OreRarity.UNCOMMON); // [Apatite]
        registerOre("azurite", 0x00095c, OreRarity.UNCOMMON); // Copper - [Azurite]
        registerOre("corderoite", 0xc28cab, OreRarity.EXTREMELY_RARE); // Mercury, Sulphur - [Cinnabar]
        registerOre("garnet", 0xa32017, OreRarity.COMMON); // [Garnet]
        registerOre("jadeite", 0x009c4b, OreRarity.RARE); // [Jade]
        registerOre("lazurite", 0x001363, OreRarity.VERY_RARE); // [Lazurite, Lapis Lazuli]
        registerOre("moissanite", 0x6e767d, OreRarity.EXTREMELY_RARE); // [Moissanite]
        registerOre("lithiophilite", 0xc9a36d, OreRarity.VERY_RARE); // Lithium, Manganese - [Purpurite]
        registerOre("arsenopyrite", 0x807970, OreRarity.COMMON); // Iron, Arsen, Sulphur - [Scorodite]
        registerOre("vanadinite", 0x8a2000, OreRarity.VERY_RARE); // Vanadium, Lead - [Wulfenite]

        // Alloys
        registerAlloy("steel", 0x707070, DEFAULT_TOOL_STATS); // Iron, Coal
        registerAlloy("wrought-iron", 0x242020, DEFAULT_TOOL_STATS); // Steel, Iron
        registerAlloy("pig-iron", 0xe3ccb1, DEFAULT_TOOL_STATS); // Wrought Iron, Sand
        registerAlloy("stainless-steel", 0xe0e0e0, DEFAULT_TOOL_STATS); // Steel, Chromium, Nickel, Molybdenum
        registerAlloy("spring-steel", 0xb5b5b5, DEFAULT_TOOL_STATS); // Steel, Silicon
        registerAlloy("tungsten-steel", 0xbcccd1, DEFAULT_TOOL_STATS); // Steel, Tungsten
        registerAlloy("vanadium-steel", 0x344234, DEFAULT_TOOL_STATS); // Steel, Vanadium
        registerAlloy("invar", 0x9c9a84, DEFAULT_TOOL_STATS); // Iron, Nickel
        registerAlloy("maraging-steel-1", 0x98bad9, DEFAULT_TOOL_STATS); // Steel, Nickel, Cobalt, Molybdenum, Aluminum
        registerAlloy("maraging-steel-2", 0x284866, DEFAULT_TOOL_STATS); // Steel, Nickel, Cobalt, Molybdenum, Titanium
        registerAlloy("maraging-steel-3", 0x677d91, DEFAULT_TOOL_STATS); // Steel, Nickel, Cobalt, Molybdenum, Niobium
        registerAlloy("manganese-steel", 0x211d24, DEFAULT_TOOL_STATS); // Steel, Manganese
        registerAlloy("bronze", 0xd9a84e, DEFAULT_TOOL_STATS); // Copper, Tin
        registerAlloy("brass", 0xc9b134, DEFAULT_TOOL_STATS); // Copper, Zinc
        registerAlloy("zamak", 0xabd1de, DEFAULT_TOOL_STATS); // Aluminum, Zinc
        registerAlloy("aluminum-scandium", 0xd0f1f5, DEFAULT_TOOL_STATS); // Aluminum, Scandium
        registerAlloy("aluminum-zirconium", 0xdaf4f7, DEFAULT_TOOL_STATS); // Aluminum, Zirconium
        registerAlloy("aluminum-magnesium", 0x5a676b, DEFAULT_TOOL_STATS); // Aluminum, Magnesium
        registerAlloy("aluminum-magnesium-zinc", 0x998776, DEFAULT_TOOL_STATS); // Aluminum, Magnesium, Zinc
        registerAlloy("nichrome", 0x526275, DEFAULT_TOOL_STATS); // Nickel, Chromium
        registerAlloy("cobalt-chromium", 0x010538, DEFAULT_TOOL_STATS); // Cobalt, Chromium
        registerAlloy("cupronickel", 0x9c7a59, DEFAULT_TOOL_STATS); // Copper, Nickel
        registerAlloy("solder", 0xc8c5ed, DEFAULT_TOOL_STATS); // Lead, Tin
        registerAlloy("nitinol", 0x918a9e, DEFAULT_TOOL_STATS); // Nickel, Titanium
        registerAlloy("spring-copper", 0xff7e33, DEFAULT_TOOL_STATS); // Copper, Beryllium
        registerAlloy("titanium-8al-1mo-1v", 0x1b0933, DEFAULT_TOOL_STATS); // Titanium, Aluminum, Molybdenum, Vanadium
        registerAlloy("titanium-6al-2sn-4zr-2mo", 0x1e152b, DEFAULT_TOOL_STATS); // Titanium, Aluminum, Tin, Zirconium, Molybdenum
        registerAlloy("titanium-6al-4v", 0x180630, DEFAULT_TOOL_STATS); // Titanium, Aluminum, Vanadium
        registerAlloy("titanium-6al-7nb", 0x270f4a, DEFAULT_TOOL_STATS); // Titanium, Aluminum, Niobium
        registerAlloy("titanium-10v-2fe-3al", 0x0d021c, DEFAULT_TOOL_STATS); // Titanium, Vanadium, Iron, Aluminum
        registerAlloy("niobium-titanium", 0x1e1e26, DEFAULT_TOOL_STATS); // Niobium, Titanium
        registerAlloy("electrum", 0xfff5a6, DEFAULT_TOOL_STATS); // Gold, Silver
        registerAlloy("mischmetal", 0x496154, DEFAULT_TOOL_STATS); // Cerium, Lanthanum, Neodymium
        registerAlloy("terbium-dysprosium", 0x7f81a3, DEFAULT_TOOL_STATS); // Terbium, Dysprosium
        registerAlloy("neodymium-iron-boron", 0xf7f6eb, DEFAULT_TOOL_STATS); // Neodymium, Iron, Boron
        registerAlloy("lanthanum-nickel", 0x8f8c6e, DEFAULT_TOOL_STATS); // Lanthanum, Nickel
        registerAlloy("copper-praseodymium", 0x756b59, DEFAULT_TOOL_STATS); // Copper, Praseodymium
        registerAlloy("samarium-cobalt", 0x86959e, DEFAULT_TOOL_STATS); // Samarium, Cobalt
        registerAlloy("gadolinium-silicon-germanium", 0x64757d, DEFAULT_TOOL_STATS); // Gadolinium, Silicon, Germanium
        registerAlloy("pewter", 0xbecfce, DEFAULT_TOOL_STATS); // Tin, Antimony
        registerAlloy("arsenic-lead", 0x77747d, DEFAULT_TOOL_STATS); // Arsenic, Lead
        registerAlloy("gallium-arsenide", 0x434d49, DEFAULT_TOOL_STATS); // Gallium, Arsenic

        // Vanilla Items
        registerVanilla("iron", 0xffffff, DEFAULT_TOOL_STATS);
        registerVanilla("copper", 0xe77c56, DEFAULT_TOOL_STATS);
        registerVanilla("gold", 0xfdf55f, DEFAULT_TOOL_STATS);
        registerVanilla("netherite", 0x31292a, DEFAULT_TOOL_STATS);
        registerVanilla("diamond", 0xa1fbe8, DEFAULT_TOOL_STATS);
        registerVanilla("emerald", 0x17dd62, DEFAULT_TOOL_STATS);

        // Gemstones
        registerGem("ruby", 0xD21F3C, DEFAULT_TOOL_STATS);
        registerGem("sapphire", 0x2855C5, DEFAULT_TOOL_STATS);
        registerGem("peridot", 0x8FBF3F, DEFAULT_TOOL_STATS);
        registerGem("aquamarine", 0x69D9D0, DEFAULT_TOOL_STATS);
        registerGem("apatite", 0x82fff9, DEFAULT_TOOL_STATS);
        registerGem("azurite", 0x00095c, DEFAULT_TOOL_STATS);
        registerGem("red-beryl", 0xc7354a, DEFAULT_TOOL_STATS);
        registerGem("morganite", 0xe8a9a5, DEFAULT_TOOL_STATS);
        registerGem("chrysoberyl", 0xe2ff52, DEFAULT_TOOL_STATS);
        registerGem("cinnabar", 0xbf3232, DEFAULT_TOOL_STATS);
        registerGem("clinohumite", 0x9c3111, DEFAULT_TOOL_STATS);
        registerGem("dioptase", 0x2c9980, DEFAULT_TOOL_STATS);
        registerGem("ekanite", 0x53784e, DEFAULT_TOOL_STATS);
        registerGem("amazonite", 0x00ffea, DEFAULT_TOOL_STATS);
        registerGem("moonstone", 0xe0f6ff, DEFAULT_TOOL_STATS);
        registerGem("sunstone", 0xffab5c, DEFAULT_TOOL_STATS);
        registerGem("fluorite", 0xe4c9ff, DEFAULT_TOOL_STATS);
        registerGem("garnet", 0xa32017, DEFAULT_TOOL_STATS);
        registerGem("helenite", 0x59ff78, DEFAULT_TOOL_STATS);
        registerGem("jade", 0x009c4b, DEFAULT_TOOL_STATS);
        registerGem("jasper", 0xff7000, DEFAULT_TOOL_STATS);
        registerGem("lazulite", 0x001b91, DEFAULT_TOOL_STATS);
        registerGem("lazurite", 0x001363, DEFAULT_TOOL_STATS);
        registerGem("malachite", 0x56cc9b, DEFAULT_TOOL_STATS);
        registerGem("moissanite", 0x6e767d, DEFAULT_TOOL_STATS);
        registerGem("opal", 0xffffff, DEFAULT_TOOL_STATS);
        registerGem("black-opal", 0x141414, DEFAULT_TOOL_STATS);
        registerGem("fire-opal", 0xb53300, DEFAULT_TOOL_STATS);
        registerGem("purpurite", 0x6600ba, DEFAULT_TOOL_STATS);
        registerGem("citrine", 0xfffa9e, DEFAULT_TOOL_STATS);
        registerGem("scorodite", 0x1e396b, DEFAULT_TOOL_STATS);
        registerGem("hiddenite", 0x84d193, DEFAULT_TOOL_STATS);
        registerGem("titanite", 0xc7db51, DEFAULT_TOOL_STATS);
        registerGem("topaz", 0xff892e, DEFAULT_TOOL_STATS);
        registerGem("triplite", 0xff7e47, DEFAULT_TOOL_STATS);
        registerGem("turquoise", 0x40e0d0, DEFAULT_TOOL_STATS);
        registerGem("wulfenite", 0xff8469, DEFAULT_TOOL_STATS);
        registerGem("zircon", 0xd9c3bf, DEFAULT_TOOL_STATS);

        // Fissions
        registerFission("uranium-238", 0x00702d, 0);
        registerFission("uranium-235", 0x004c20, 1);
        registerFission("neptunium-235", 0x4e7a96, 2);
        registerFission("plutonium-236", 0x102273, 3);
        registerFission("plutonium-239", 0x071346, 4);
        registerFission("americium-241", 0x928e7f, 5);
        registerFission("curium-245", 0x8b9ca2, 6);
        registerFission("berkelium-249", 0x798076, 7);
        registerFission("californium-252", 0x746868, 8);

        // Cables
        registerCables();

        // Solar Panels
        registerSolarPanels();

        // Batteries
        registerBatteries();

        // Coils
        registerMajorTierItems(12, "coil", COIL_MAP, COIL_COLOR_MAP);

        // Sacks
        registerSacks();

        // MultiBlock Blocks
        registerMBBlocks();
    }

    private static void registerCustomItem(String name) {
        RegistryObject<Item> item = ITEMS.register(name, () -> new Item(new Item.Properties()));
        CUSTOM_ITEM_MAP.put(name, item);
    }

    private static <T extends Item> RegistryObject<T> registerCustomItem(String name, Function<Item.Properties, T> factory) {
        RegistryObject<T> item = ITEMS.register(name, () -> factory.apply(new Item.Properties()));
        CUSTOM_ITEM_MAP.put(name, item);
        return item;
    }

    private static Item createToolPartItem(String component, ResourceLocation repairMaterialId, ModularToolStats stats) {
        if (component.endsWith("-head")) {
            return ModularToolPartItem.head(stats.baseDurability, stats.miningSpeed, repairMaterialId);
        }
        if (component.equals("rod")) {
            return ModularToolPartItem.rod(stats.durabilityModifier);
        }
        return new Item(new Item.Properties());
    }

    public static final String[] MATERIAL_COMPONENT_NAMES = {"ingot", "dust", "gear", "nugget", "plate", "rod", "raw", "pickaxe-head"};

    private static void registerMaterial(String materialName, int color, ModularToolStats stats) {
        ResourceLocation repairMaterialId = new ResourceLocation(MetallurgyPlus.MODID, materialName + "_ingot");
        for (String component : MATERIAL_COMPONENT_NAMES) {
            String name = materialName + "_" + component;
            RegistryObject<Item> item = ITEMS.register(name,
                () -> createToolPartItem(component, repairMaterialId, stats));
            MATERIAL_MAP.put(name, item);
            MATERIAL_COLOR_MAP.put(name, color);
        }

        String name = materialName + "_block";
        RegistryObject<Block> block = ModBlocks.registerBlock(name,
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL)));

        ModBlocks.MATERIAL_BLOCKS_MAP.put(name, block);
        ModBlocks.MATERIAL_COLOR_MAP.put(name, color);
    }

    public static final String[] ORE_COMPONENT_NAMES = {"raw"};
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
            RegistryObject<Block> block = ModBlocks.registerOreBlock(name,
                () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE).sound(SoundType.STONE)),
                oreRarity);

            ModBlocks.ORE_BLOCKS_MAP.put(name, block);
            ModBlocks.ORE_COLOR_MAP.put(name, color);
            ModBlocks.ORE_RARITY_MAP.put(block, oreRarity);
        }
    }

    public static final String[] ALLOY_COMPONENT_NAMES = {"ingot", "gear", "nugget", "plate", "rod", "pickaxe-head"};

    private static void registerAlloy(String materialName, int color, ModularToolStats stats) {
        ResourceLocation repairMaterialId = new ResourceLocation(MetallurgyPlus.MODID, materialName + "_ingot");
        for (String component : ALLOY_COMPONENT_NAMES) {
            String name = materialName + "_" + component;
            RegistryObject<Item> item = ITEMS.register(name,
                () -> createToolPartItem(component, repairMaterialId, stats));
            ALLOY_MAP.put(name, item);
            ALLOY_COLOR_MAP.put(name, color);
        }

        String name = materialName + "_block";
        RegistryObject<Block> block = ModBlocks.registerBlock(name,
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL)));

        ModBlocks.ALLOY_BLOCKS_MAP.put(name, block);
        ModBlocks.ALLOY_COLOR_MAP.put(name, color);
    }

    public static final String[] VANILLA_COMPONENT_NAMES = {"dust", "gear", "plate", "rod", "pickaxe-head"};

    private static void registerVanilla(String materialName, int color, ModularToolStats stats) {
        ResourceLocation repairMaterialId = getVanillaRepairMaterialId(materialName);
        for (String component : VANILLA_COMPONENT_NAMES) {
            String name = materialName + "_" + component;
            RegistryObject<Item> item = ITEMS.register(name,
                () -> createToolPartItem(component, repairMaterialId, stats));
            VANILLA_MAP.put(name, item);
            VANILLA_COLOR_MAP.put(name, color);
        }
    }

    public static final String[] GEM_COMPONENT_NAMES = {"gem", "rod", "pickaxe-head"};

    private static void registerGem(String gemName, int color, ModularToolStats stats) {
        ResourceLocation repairMaterialId = new ResourceLocation(MetallurgyPlus.MODID, gemName + "_gem");
        for (String component : GEM_COMPONENT_NAMES) {
            String name = gemName + "_" + component;
            RegistryObject<Item> item = ITEMS.register(name,
                () -> createToolPartItem(component, repairMaterialId, stats));
            GEM_MAP.put(name, item);
            GEM_COLOR_MAP.put(name, color);
        }

        String name = gemName + "_block";
        RegistryObject<Block> block = ModBlocks.registerBlock(name,
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.EMERALD_BLOCK).sound(SoundType.METAL)));

        ModBlocks.GEM_BLOCKS_MAP.put(name, block);
        ModBlocks.GEM_COLOR_MAP.put(name, color);
    }

    private static ResourceLocation getVanillaRepairMaterialId(String materialName) {
        String itemName = switch (materialName) {
            case "diamond", "emerald" -> materialName;
            default -> materialName + "_ingot";
        };
        return new ResourceLocation("minecraft", itemName);
    }

    public static final String[] FISSION_COMPONENT_NAMES = {"pellet", "chunk", "fuel", "fuel-depleted"};

    private static void registerFission(String fissionName, int color, int fissionTier) {
        FISSION_TIER_LIST.add(fissionTier, fissionName);

        for (String component : FISSION_COMPONENT_NAMES) {
            String name = fissionName + "_" + component;
            RegistryObject<Item> item = ITEMS.register(name, () ->
                new Item(new Item.Properties())
            );
            FISSION_MAP.put(name, item);

            if (component.equals("fuel-depleted")) {
                color = Utility.darkenColor(color, 0.75f);
            }

            FISSION_COLOR_MAP.put(name, color);
        }
    }

    private static void registerTierItem(int tier, int color, String itemName, Map<Integer, RegistryObject<Item>> itemMap, Map<Integer, Integer> colorMap) {
        String name = itemName + tier;
        RegistryObject<Item> item = ITEMS.register(name, () -> new Item(new Item.Properties()));

        itemMap.put(tier, item);
        colorMap.put(tier, color);
    }

    private static void registerMajorTierItems(int maxTier, String name, Map<Integer, RegistryObject<Item>> itemMap, Map<Integer, Integer> colorMap) {
        for (int i = 1; i <= maxTier; i++) {
            registerTierItem(i, MAJOR_TIER_COLORS.get(i), name, itemMap, colorMap);
        }
    }

    private static void registerCable(String cableName, int tier, int displayTier, int color, BlockBehaviour.Properties properties) {
        String name = cableName + displayTier + "_block";
        RegistryObject<CableBlock> block = ModBlocks.registerBlock(name,
            () -> new CableBlock(properties, tier));
        ModBlocks.CABLE_BLOCKS_MAP.put(tier, block);
        ModBlocks.CABLE_COLOR_MAP.put(tier, color);
    }

    private static void registerCables() {
        for (int i = 1; i <= 12; i++) {
            registerCable("cable", i, i, MAJOR_TIER_COLORS.get(i), BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.WOOL));
        }

        for (int i = 13; i <= 24; i++) {
            int displayTier = i - 12;
            registerCable("resonant-cable", i, displayTier, MAJOR_TIER_COLORS.get(displayTier), BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL));
        }

        for (int i = 25; i <= 36; i++) {
            int displayTier = i - 24;
            registerCable("superconductor", i, displayTier, MAJOR_TIER_COLORS.get(displayTier), BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.AMETHYST));
        }
    }

    private static void registerSolarPanel(int tier, int color) {
        String name = "solar_panel" + tier + "_block";
        RegistryObject<SolarPanelBlock> block = ModBlocks.registerBlock(name,
            () -> new SolarPanelBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL), tier));
        ModBlocks.SOLAR_PANEL_BLOCK_MAP.put(tier, block);
        ModBlocks.SOLAR_PANEL_COLOR_MAP.put(tier, color);
    }

    private static void registerSolarPanels() {
        for (int i = 1; i <= TIER_COLORS.size() - 1; i++) {
            registerSolarPanel(i, TIER_COLORS.get(i));
        }
    }

    private static void registerBattery(int tier, int color) {
        String name = "battery" + tier + "_block";
        RegistryObject<BatteryBlock> block = ModBlocks.registerBlock(name,
            () -> new BatteryBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL), tier));
        ModBlocks.BATTERY_BLOCK_MAP.put(tier, block);
        ModBlocks.BATTERY_COLOR_MAP.put(tier, color);
    }

    private static void registerBatteries() {
        for (int i = 1; i <= 7; i++) {
            registerBattery(i, MAJOR_TIER_COLORS.get(i));
        }
    }

    private static void registerSack(int tier, int color) {
        String name = "sack" + tier;
        RegistryObject<Item> item = ITEMS.register(name, () -> new SackItem(new Item.Properties().stacksTo(1), tier));

        SACK_MAP.put(tier, item);
        SACK_COLOR_MAP.put(tier, color);
    }

    private static void registerSacks() {
        for (int i = 1; i <= 8; i++) {
            registerSack(i, MAJOR_TIER_COLORS.get(i));
        }
    }

    private static void registerMBBlocks() {
        registerMBBatteryBlocks();
        registerMBReactorBlocks();
    }

    private static void appendTieredBlocks(List<String> blockNames, String name, int tier, Map<String, Integer> colorMap, List<Integer> colors) {
        for (int i = 1; i <= tier; i++) {
            String blockName = name + i;
            blockNames.add(blockName);
            colorMap.put(blockName, colors.get(i));
        }
    }

    private static void registerMBBatteryBlocks() {
        List<String> blockNames = new ArrayList<>(List.of(
            "battery/controller",
            "battery/casing",
            "battery/energy_input",
            "battery/energy_output"
        ));

        assert blockNames.contains("battery/controller");

        appendTieredBlocks(blockNames, "battery/cell", 12, ModBlocks.MULTIBLOCK_COLOR_MAP, MAJOR_TIER_COLORS);
        appendTieredBlocks(blockNames, "battery/resonant-cell", 12, ModBlocks.MULTIBLOCK_COLOR_MAP, MAJOR_TIER_COLORS);
        appendTieredBlocks(blockNames, "battery/superconductor-cell", 12, ModBlocks.MULTIBLOCK_COLOR_MAP, MAJOR_TIER_COLORS);

        ModBlocks.registerMapBlocks(ModBlocks.MULTIBLOCKS_MAP, blockNames, name -> {
            BlockBehaviour.Properties properties = BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL);

            if (name.contains("cell")) {
                int tier = Utility.getTier(name);

                if (name.contains("resonant-cell")) {
                    tier += 12;
                } else if (name.contains("superconductor-cell")) {
                    tier += 24;
                }

                return new BatteryCellBlock(properties, tier);
            }

            Supplier<? extends BlockEntityType<? extends MBControllerBlockEntity>> beSupplier = ModBlockEntities.CONTROLLER_TYPES.get(name);
            if (beSupplier != null) {
                return new MBControllerBlock(properties, beSupplier);
            }

            if (name.contains("energy")) {
                return new MBEnergyPortBlock(properties, ModBlockEntities::getEnergyPortBlockEntityType);
            }

            return new Block(properties);
        });
    }

    private static void registerMBReactorBlocks() {
        List<String> blockNames = new ArrayList<>(List.of(
            "reactor/controller",
            "reactor/casing",
            "reactor/glass",
            "reactor/fuel_cell",
            "reactor/energy_output",
            "reactor/item_input",
            "reactor/item_output",
            "reactor/fluid_input"
        ));

        assert blockNames.contains("reactor/controller");

        ModBlocks.registerMapBlocks(ModBlocks.MULTIBLOCKS_MAP, blockNames, name -> {
            BlockBehaviour.Properties properties = BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL);

            Supplier<? extends BlockEntityType<? extends MBControllerBlockEntity>> beSupplier = ModBlockEntities.CONTROLLER_TYPES.get(name);
            if (beSupplier != null) {
                return new MBControllerBlock(properties, beSupplier);
            }

            if (name.contains("energy")) {
                return new MBEnergyPortBlock(properties, ModBlockEntities::getEnergyPortBlockEntityType);
            } else if (name.contains("item")) {
                return new MBItemPortBlock(
                    properties,
                    ModBlockEntities::getItemPortBlockEntityType,
                    name.endsWith("/item_input")
                );
            } else if (name.contains("fluid")) {
                return new MBFluidPortBlock(properties, ModBlockEntities::getFluidPortBlockEntityType);
            } else if (name.contains("fuel_cell") || name.contains("glass")) {
                properties = BlockBehaviour.Properties.copy(Blocks.GLASS).sound(SoundType.GLASS);
                return new Block(properties);
            }

            return new Block(properties);
        });
    }
}
