package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    private AlloySmelterRecipeProvider alloySmelterRecipeProvider;
    private OreProcessingUnitRecipeProvider oreProcessingUnitRecipeProvider;
    private GrinderRecipeProvider grinderRecipeProvider;
    private PressRecipeProvider pressRecipeProvider;
    private ExtractorRecipeProvider extractorRecipeProvider;

    private static final List<ItemLike> CLAY_SMELTABLES = List.of(ModItems.CUSTOM_ITEM_MAP.get("clay_mineral_raw").get());

    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
        alloySmelterRecipeProvider = new AlloySmelterRecipeProvider(pOutput);
        oreProcessingUnitRecipeProvider = new OreProcessingUnitRecipeProvider(pOutput);
        grinderRecipeProvider = new GrinderRecipeProvider(pOutput);
        pressRecipeProvider = new PressRecipeProvider(pOutput);
        extractorRecipeProvider = new ExtractorRecipeProvider(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        buildCustomRecipes(pWriter);

        buildMaterialRecipes(pWriter);
        buildOreRecipes(pWriter);
        buildAlloyRecipes(pWriter);
        buildVanillaRecipes(pWriter);

        alloySmelterRecipeProvider.buildRecipes(pWriter);
        oreProcessingUnitRecipeProvider.buildRecipes(pWriter);
        grinderRecipeProvider.buildRecipes(pWriter);
        pressRecipeProvider.buildRecipes(pWriter);
        extractorRecipeProvider.buildRecipes(pWriter);

        buildCableRecipes(pWriter);
        buildCoilRecipes(pWriter);
        buildSolarPanelRecipes(pWriter);
        buildBatteryRecipes(pWriter);
        buildSackRecipes(pWriter);
        buildBlockEntitiesRecipes(pWriter);
    }

    private void buildMaterialRecipes(Consumer<FinishedRecipe> pWriter) {
        List<String> oldMaterials = new ArrayList<>();
        for (RegistryObject<Item> item : ModItems.MATERIAL_MAP.values()) {
            String currentName = item.getId().getPath();
            String currentMaterialName = currentName.split("_")[0];
            if (!oldMaterials.contains(currentMaterialName)) {
                oldMaterials.add(currentMaterialName);

                Item ingot = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[0]).get();
                Item dust = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[1]).get();
                Item gear = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[2]).get();
                Item nugget = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[3]).get();
                Item plate = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[4]).get();
                Item rod = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[5]).get();
                Item raw = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[6]).get();

                Block block = ModBlocks.MATERIAL_BLOCKS_MAP.get(currentMaterialName + "_block").get();

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, block)
                    .pattern("XXX")
                    .pattern("XXX")
                    .pattern("XXX")
                    .define('X', ingot)
                    .unlockedBy(getHasName(ingot), has(ingot))
                    .save(pWriter);

                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ingot, 9)
                    .requires(block)
                    .unlockedBy(getHasName(block), has(block))
                    .save(pWriter, new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_ingots_from_block"));

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ingot)
                    .pattern("XXX")
                    .pattern("XXX")
                    .pattern("XXX")
                    .define('X', nugget)
                    .unlockedBy(getHasName(nugget), has(nugget))
                    .save(pWriter, new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_ingot_from_nuggets"));

                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nugget, 9)
                    .requires(ingot)
                    .unlockedBy(getHasName(ingot), has(ingot))
                    .save(pWriter);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, gear)
                    .pattern(" X ")
                    .pattern("X X")
                    .pattern(" X ")
                    .define('X', ingot)
                    .unlockedBy(getHasName(ingot), has(ingot))
                    .save(pWriter);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, rod)
                    .pattern("   ")
                    .pattern("  X")
                    .pattern(" X ")
                    .define('X', ingot)
                    .unlockedBy(getHasName(ingot), has(ingot))
                    .save(pWriter);

                oreSmelting(pWriter, List.of(raw), RecipeCategory.MISC, ingot, 0.25f, 100, currentMaterialName);
                oreSmelting(pWriter, List.of(dust), RecipeCategory.MISC, ingot, 0.25f, 100, currentMaterialName);
            }
        }
    }

    private void buildOreRecipes(Consumer<FinishedRecipe> pWriter) {
        List<String> oldMaterials = new ArrayList<>();
        for (RegistryObject<Item> item : ModItems.ORE_MAP.values()) {
            String currentName = item.getId().getPath();
            String currentMaterialName = currentName.split("_")[0];
            if (!oldMaterials.contains(currentMaterialName)) {
                oldMaterials.add(currentMaterialName);

                Item raw = ModItems.ORE_MAP.get(currentMaterialName + "_" + ModItems.ORE_COMPONENT_NAMES[0]).get();
                Item dust = ModItems.ORE_MAP.get(currentMaterialName + "_" + ModItems.ORE_COMPONENT_NAMES[1]).get();

            }
        }
    }

    private void buildAlloyRecipes(Consumer<FinishedRecipe> pWriter) {
        List<String> oldMaterials = new ArrayList<>();
        for (RegistryObject<Item> item : ModItems.ALLOY_MAP.values()) {
            String currentName = item.getId().getPath();
            String currentMaterialName = currentName.split("_")[0];
            if (!oldMaterials.contains(currentMaterialName)) {
                oldMaterials.add(currentMaterialName);

                Item ingot = ModItems.ALLOY_MAP.get(currentMaterialName + "_" + ModItems.ALLOY_COMPONENT_NAMES[0]).get();
                Item gear = ModItems.ALLOY_MAP.get(currentMaterialName + "_" + ModItems.ALLOY_COMPONENT_NAMES[1]).get();
                Item nugget = ModItems.ALLOY_MAP.get(currentMaterialName + "_" + ModItems.ALLOY_COMPONENT_NAMES[2]).get();
                Item plate = ModItems.ALLOY_MAP.get(currentMaterialName + "_" + ModItems.ALLOY_COMPONENT_NAMES[3]).get();
                Item rod = ModItems.ALLOY_MAP.get(currentMaterialName + "_" + ModItems.ALLOY_COMPONENT_NAMES[4]).get();

                Block block = ModBlocks.ALLOY_BLOCKS_MAP.get(currentMaterialName + "_block").get();

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, block)
                    .pattern("XXX")
                    .pattern("XXX")
                    .pattern("XXX")
                    .define('X', ingot)
                    .unlockedBy(getHasName(ingot), has(ingot))
                    .save(pWriter);

                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ingot, 9)
                    .requires(block)
                    .unlockedBy(getHasName(block), has(block))
                    .save(pWriter, new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_ingots_from_block"));

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ingot)
                    .pattern("XXX")
                    .pattern("XXX")
                    .pattern("XXX")
                    .define('X', nugget)
                    .unlockedBy(getHasName(nugget), has(nugget))
                    .save(pWriter, new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_ingot_from_nuggets"));

                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nugget, 9)
                    .requires(ingot)
                    .unlockedBy(getHasName(ingot), has(ingot))
                    .save(pWriter);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, gear)
                    .pattern(" X ")
                    .pattern("X X")
                    .pattern(" X ")
                    .define('X', ingot)
                    .unlockedBy(getHasName(ingot), has(ingot))
                    .save(pWriter);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, rod)
                    .pattern("   ")
                    .pattern("  X")
                    .pattern(" X ")
                    .define('X', ingot)
                    .unlockedBy(getHasName(ingot), has(ingot))
                    .save(pWriter);
            }
        }
    }

    private void buildVanillaRecipes(Consumer<FinishedRecipe> pWriter) {
        List<String> oldMaterials = new ArrayList<>();
        for (RegistryObject<Item> item : ModItems.VANILLA_MAP.values()) {
            String currentName = item.getId().getPath();
            String currentMaterialName = currentName.split("_")[0];
            if (!oldMaterials.contains(currentMaterialName)) {
                oldMaterials.add(currentMaterialName);

                Item ingot = Utility.getItem(currentMaterialName + "_ingot");
                if (ingot == Items.AIR) {
                    ingot = Utility.getItem(currentMaterialName);
                    if (ingot == Items.AIR) continue;
                }

                Item dust = ModItems.VANILLA_MAP.get(currentMaterialName + "_" + ModItems.VANILLA_COMPONENT_NAMES[0]).get();
                Item gear = ModItems.VANILLA_MAP.get(currentMaterialName + "_" + ModItems.VANILLA_COMPONENT_NAMES[1]).get();
                Item plate = ModItems.VANILLA_MAP.get(currentMaterialName + "_" + ModItems.VANILLA_COMPONENT_NAMES[2]).get();
                Item rod = ModItems.VANILLA_MAP.get(currentMaterialName + "_" + ModItems.VANILLA_COMPONENT_NAMES[3]).get();

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, gear)
                    .pattern(" X ")
                    .pattern("X X")
                    .pattern(" X ")
                    .define('X', ingot)
                    .unlockedBy(getHasName(ingot), has(ingot))
                    .save(pWriter);

                if (ingot == Items.IRON_INGOT) {
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, rod)
                        .pattern("   ")
                        .pattern(" X ")
                        .pattern(" X ")
                        .define('X', ingot)
                        .unlockedBy(getHasName(ingot), has(ingot))
                        .save(pWriter);
                } else {
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, rod)
                        .pattern("   ")
                        .pattern("  X")
                        .pattern(" X ")
                        .define('X', ingot)
                        .unlockedBy(getHasName(ingot), has(ingot))
                        .save(pWriter);
                }

                oreSmelting(pWriter, List.of(dust), RecipeCategory.MISC, ingot, 0.25f, 100, currentMaterialName);
            }
        }
    }

    private void buildCustomRecipes(Consumer<FinishedRecipe> pWriter) {
        oreSmelting(pWriter, CLAY_SMELTABLES, RecipeCategory.MISC, Items.CLAY_BALL, 0.25f, 100, "clay");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CUSTOM_ITEM_MAP.get("small_rare_earth").get(), 1)
            .requires(ModItems.CUSTOM_ITEM_MAP.get("rare_earth_1").get())
            .requires(ModItems.CUSTOM_ITEM_MAP.get("rare_earth_2").get())
            .requires(ModItems.CUSTOM_ITEM_MAP.get("rare_earth_3").get())
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("rare_earth_1").get()), has(ModItems.CUSTOM_ITEM_MAP.get("rare_earth_1").get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("rare_earth_2").get()), has(ModItems.CUSTOM_ITEM_MAP.get("rare_earth_2").get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("rare_earth_3").get()), has(ModItems.CUSTOM_ITEM_MAP.get("rare_earth_3").get()))
            .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CUSTOM_ITEM_MAP.get("mixed_rare_earth_alloy").get(), 1)
            .requires(ModItems.ALLOY_MAP.get("mischmetal_ingot").get())
            .requires(ModItems.ALLOY_MAP.get("terbium-dysprosium_ingot").get())
            .requires(ModItems.ALLOY_MAP.get("neodymium-iron-boron_ingot").get())
            .requires(ModItems.ALLOY_MAP.get("lanthanum-nickel_ingot").get())
            .requires(ModItems.ALLOY_MAP.get("copper-praseodymium_ingot").get())
            .requires(ModItems.ALLOY_MAP.get("samarium-cobalt_ingot").get())
            .requires(ModItems.ALLOY_MAP.get("gadolinium-silicon-germanium_ingot").get())
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("mischmetal_ingot").get()), has(ModItems.ALLOY_MAP.get("mischmetal_ingot").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("terbium-dysprosium_ingot").get()), has(ModItems.ALLOY_MAP.get("terbium-dysprosium_ingot").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("neodymium-iron-boron_ingot").get()), has(ModItems.ALLOY_MAP.get("neodymium-iron-boron_ingot").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("lanthanum-nickel_ingot").get()), has(ModItems.ALLOY_MAP.get("lanthanum-nickel_ingot").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("copper-praseodymium_ingot").get()), has(ModItems.ALLOY_MAP.get("copper-praseodymium_ingot").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("samarium-cobalt_ingot").get()), has(ModItems.ALLOY_MAP.get("samarium-cobalt_ingot").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("gadolinium-silicon-germanium_ingot").get()), has(ModItems.ALLOY_MAP.get("gadolinium-silicon-germanium_ingot").get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.COBBLESTONE, 1)
            .pattern("SS ")
            .pattern("SS ")
            .pattern("   ")
            .define('S', ModItems.CUSTOM_ITEM_MAP.get("stone_dust").get())
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("stone_dust").get()), has(ModItems.CUSTOM_ITEM_MAP.get("stone_dust").get()))
            .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PAPER, 2)
            .requires(ModItems.CUSTOM_ITEM_MAP.get("saw_dust").get(), 3)
            .requires(Items.WATER_BUCKET)
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("saw_dust").get()), has(ModItems.CUSTOM_ITEM_MAP.get("saw_dust").get()))
            .unlockedBy(getHasName(Items.WATER_BUCKET), has(Items.WATER_BUCKET))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CUSTOM_ITEM_MAP.get("energy_core").get(), 1)
            .pattern("ICI")
            .pattern("GRG")
            .pattern("ICI")
            .define('I', Items.IRON_INGOT)
            .define('C', Items.COPPER_INGOT)
            .define('G', Items.GOLD_INGOT)
            .define('R', Items.REDSTONE_BLOCK)
            .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
            .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
            .unlockedBy(getHasName(Items.GOLD_INGOT), has(Items.GOLD_INGOT))
            .unlockedBy(getHasName(Items.REDSTONE_BLOCK), has(Items.REDSTONE_BLOCK))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MACHINE_FRAME.get(), 1)
            .pattern("IGI")
            .pattern("GCG")
            .pattern("IGI")
            .define('I', Items.IRON_INGOT)
            .define('G', Items.GLASS)
            .define('C', ModItems.CUSTOM_ITEM_MAP.get("energy_core").get())
            .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
            .unlockedBy(getHasName(Items.GLASS), has(Items.GLASS))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("energy_core").get()), has(ModItems.CUSTOM_ITEM_MAP.get("energy_core").get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get(), 1)
            .pattern("GGG")
            .pattern("LQL")
            .pattern("III")
            .define('G', Items.GLASS)
            .define('L', Items.LAPIS_LAZULI)
            .define('Q', Items.QUARTZ)
            .define('I', Items.IRON_INGOT)
            .unlockedBy(getHasName(Items.GLASS), has(Items.GLASS))
            .unlockedBy(getHasName(Items.LAPIS_LAZULI), has(Items.LAPIS_LAZULI))
            .unlockedBy(getHasName(Items.QUARTZ), has(Items.QUARTZ))
            .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
            .save(pWriter);
    }

    private void buildBlockEntitiesRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.PRESS.get())
            .pattern("IPI")
            .pattern("IFI")
            .pattern("III")
            .define('I', Items.IRON_INGOT)
            .define('F', ModBlocks.MACHINE_FRAME.get())
            .define('P', Items.PISTON)
            .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
            .unlockedBy(getHasName(ModBlocks.MACHINE_FRAME.get()), has(ModBlocks.MACHINE_FRAME.get()))
            .unlockedBy(getHasName(Items.PISTON), has(Items.PISTON))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ORE_PROCESSING_UNIT.get())
            .pattern("IDI")
            .pattern("GFG")
            .pattern("ICI")
            .define('I', ModItems.VANILLA_MAP.get("iron_plate").get())
            .define('D', ModItems.VANILLA_MAP.get("diamond_gear").get())
            .define('F', ModBlocks.MACHINE_FRAME.get())
            .define('G', ModItems.VANILLA_MAP.get("iron_gear").get())
            .define('C', ModItems.VANILLA_MAP.get("copper_gear").get())
            .unlockedBy(getHasName(ModItems.VANILLA_MAP.get("iron_plate").get()), has(ModItems.VANILLA_MAP.get("iron_plate").get()))
            .unlockedBy(getHasName(ModItems.VANILLA_MAP.get("diamond_gear").get()), has(ModItems.VANILLA_MAP.get("diamond_gear").get()))
            .unlockedBy(getHasName(ModBlocks.MACHINE_FRAME.get()), has(ModBlocks.MACHINE_FRAME.get()))
            .unlockedBy(getHasName(ModItems.VANILLA_MAP.get("iron_gear").get()), has(ModItems.VANILLA_MAP.get("iron_gear").get()))
            .unlockedBy(getHasName(ModItems.VANILLA_MAP.get("copper_gear").get()), has(ModItems.VANILLA_MAP.get("copper_gear").get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.EXTRACTOR.get())
            .pattern("NCN")
            .pattern("GFG")
            .pattern("NSN")
            .define('N', ModItems.MATERIAL_MAP.get("nickel_plate").get())
            .define('C', ModItems.MATERIAL_MAP.get("chromium_rod").get())
            .define('F', ModBlocks.MACHINE_FRAME.get())
            .define('G', ModItems.MATERIAL_MAP.get("graphite_gear").get())
            .define('S', ModItems.MATERIAL_MAP.get("selenium_plate").get())
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("nickel_plate").get()), has(ModItems.MATERIAL_MAP.get("nickel_plate").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("chromium_rod").get()), has(ModItems.MATERIAL_MAP.get("chromium_rod").get()))
            .unlockedBy(getHasName(ModBlocks.MACHINE_FRAME.get()), has(ModBlocks.MACHINE_FRAME.get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("graphite_gear").get()), has(ModItems.MATERIAL_MAP.get("graphite_gear").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("selenium_plate").get()), has(ModItems.MATERIAL_MAP.get("selenium_plate").get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ALLOY_SMELTER.get())
            .pattern("BBB")
            .pattern("LFN")
            .pattern("TIA")
            .define('B', Items.BRICK)
            .define('I', ModItems.VANILLA_MAP.get("iron_plate").get())
            .define('L', ModItems.MATERIAL_MAP.get("lead_gear").get())
            .define('F', ModBlocks.MACHINE_FRAME.get())
            .define('N', ModItems.MATERIAL_MAP.get("nickel_gear").get())
            .define('T', ModItems.MATERIAL_MAP.get("tin_gear").get())
            .define('A', ModItems.MATERIAL_MAP.get("aluminum_gear").get())
            .unlockedBy(getHasName(Items.BRICK), has(Items.BRICK))
            .unlockedBy(getHasName(ModItems.VANILLA_MAP.get("iron_plate").get()), has(ModItems.VANILLA_MAP.get("iron_plate").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("lead_gear").get()), has(ModItems.MATERIAL_MAP.get("lead_gear").get()))
            .unlockedBy(getHasName(ModBlocks.MACHINE_FRAME.get()), has(ModBlocks.MACHINE_FRAME.get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("nickel_gear").get()), has(ModItems.MATERIAL_MAP.get("nickel_gear").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("tin_gear").get()), has(ModItems.MATERIAL_MAP.get("tin_gear").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("aluminum_gear").get()), has(ModItems.MATERIAL_MAP.get("aluminum_gear").get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.GRINDER.get())
            .pattern("fPf")
            .pattern("SFS")
            .pattern("ZsZ")
            .define('f', Items.FLINT)
            .define('P', Items.PISTON)
            .define('Z', ModBlocks.ALLOY_BLOCKS_MAP.get("zamak_block").get())
            .define('F', ModBlocks.MACHINE_FRAME.get())
            .define('S', ModItems.ALLOY_MAP.get("steel_gear").get())
            .define('s', ModItems.ALLOY_MAP.get("steel_plate").get())
            .unlockedBy(getHasName(Items.FLINT), has(Items.FLINT))
            .unlockedBy(getHasName(Items.PISTON), has(Items.PISTON))
            .unlockedBy(getHasName(ModBlocks.ALLOY_BLOCKS_MAP.get("zamak_block").get()), has(ModBlocks.ALLOY_BLOCKS_MAP.get("zamak_block").get()))
            .unlockedBy(getHasName(ModBlocks.MACHINE_FRAME.get()), has(ModBlocks.MACHINE_FRAME.get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("steel_gear").get()), has(ModItems.ALLOY_MAP.get("steel_gear").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("steel_plate").get()), has(ModItems.ALLOY_MAP.get("steel_plate").get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.QUARRY.get())
            .pattern("PpP")
            .pattern("EFH")
            .pattern("PYP")
            .define('P', ModItems.CUSTOM_ITEM_MAP.get("mixed_rare_earth_alloy_plate").get())
            .define('p', ModItems.MATERIAL_MAP.get("promethium_rod").get())
            .define('E', ModItems.MATERIAL_MAP.get("europium_gear").get())
            .define('F', ModBlocks.MACHINE_FRAME.get())
            .define('H', ModItems.MATERIAL_MAP.get("holmium_gear").get())
            .define('Y', ModItems.MATERIAL_MAP.get("ytterbium_gear").get())
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("mixed_rare_earth_alloy_plate").get()), has(ModItems.CUSTOM_ITEM_MAP.get("mixed_rare_earth_alloy_plate").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("promethium_rod").get()), has(ModItems.MATERIAL_MAP.get("promethium_rod").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("europium_gear").get()), has(ModItems.MATERIAL_MAP.get("europium_gear").get()))
            .unlockedBy(getHasName(ModBlocks.MACHINE_FRAME.get()), has(ModBlocks.MACHINE_FRAME.get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("holmium_gear").get()), has(ModItems.MATERIAL_MAP.get("holmium_gear").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("ytterbium_gear").get()), has(ModItems.MATERIAL_MAP.get("ytterbium_gear").get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SACK_STATION.get())
            .pattern("IGI")
            .pattern("GFG")
            .pattern("IHI")
            .define('I', ModItems.VANILLA_MAP.get("iron_plate").get())
            .define('G', Items.GLASS)
            .define('F', ModBlocks.MACHINE_FRAME.get())
            .define('H', Items.HOPPER)
            .unlockedBy(getHasName(ModItems.VANILLA_MAP.get("iron_plate").get()), has(ModItems.VANILLA_MAP.get("iron_plate").get()))
            .unlockedBy(getHasName(Items.GLASS), has(Items.GLASS))
            .unlockedBy(getHasName(ModBlocks.MACHINE_FRAME.get()), has(ModBlocks.MACHINE_FRAME.get()))
            .unlockedBy(getHasName(Items.HOPPER), has(Items.HOPPER))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.GEMSTONE_CUTTER.get())
            .pattern("PGP")
            .pattern("RFR")
            .pattern("PBP")
            .define('P', ModItems.ALLOY_MAP.get("stainless-steel_plate").get())
            .define('G', ModItems.VANILLA_MAP.get("diamond_gear").get())
            .define('R', ModItems.ALLOY_MAP.get("titanium-6al-4v_rod").get())
            .define('F', ModBlocks.MACHINE_FRAME.get())
            .define('B', ModBlocks.ALLOY_BLOCKS_MAP.get("tungsten-steel_block").get())
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("stainless-steel_plate").get()), has(ModItems.ALLOY_MAP.get("stainless-steel_plate").get()))
            .unlockedBy(getHasName(ModItems.VANILLA_MAP.get("diamond_gear").get()), has(ModItems.VANILLA_MAP.get("diamond_gear").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("titanium-6al-4v_rod").get()), has(ModItems.ALLOY_MAP.get("titanium-6al-4v_rod").get()))
            .unlockedBy(getHasName(ModBlocks.MACHINE_FRAME.get()), has(ModBlocks.MACHINE_FRAME.get()))
            .unlockedBy(getHasName(ModBlocks.ALLOY_BLOCKS_MAP.get("tungsten-steel_block").get()), has(ModBlocks.ALLOY_BLOCKS_MAP.get("tungsten-steel_block").get()))
            .save(pWriter);
    }

    private void buildCableRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CABLE_BLOCKS_MAP.get(1).get())
            .pattern("RCR")
            .pattern("CcC")
            .pattern("RCR")
            .define('R', ModItems.CUSTOM_ITEM_MAP.get("rubber").get())
            .define('C', ModItems.VANILLA_MAP.get("copper_plate").get())
            .define('c', ModItems.COIL_MAP.get(1).get())
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("rubber").get()), has(ModItems.CUSTOM_ITEM_MAP.get("rubber").get()))
            .unlockedBy(getHasName(ModItems.VANILLA_MAP.get("copper_plate").get()), has(ModItems.VANILLA_MAP.get("copper_plate").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(1).get()), has(ModItems.COIL_MAP.get(1).get()))
            .save(pWriter);

        
    }

    private void buildCableRecipe(Consumer<FinishedRecipe> pWriter, int tier, Item plate) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CABLE_BLOCKS_MAP.get(tier).get())
            .pattern("XPX")
            .pattern("PcP")
            .pattern("XPX")
            .define('X', ModBlocks.CABLE_BLOCKS_MAP.get(tier - 1).get())
            .define('P', plate)
            .define('c', ModItems.COIL_MAP.get(tier).get())
            .unlockedBy(getHasName(ModBlocks.CABLE_BLOCKS_MAP.get(tier - 1).get()), has(ModBlocks.CABLE_BLOCKS_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(plate), has(plate))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(tier).get()), has(ModItems.COIL_MAP.get(tier).get()))
            .save(pWriter);
    }

    private void buildCoilRecipes(Consumer<FinishedRecipe> pWriter) {
        buildCoilRecipe(pWriter, 1, ModItems.VANILLA_MAP.get("iron_rod").get(), Items.COPPER_INGOT);
        buildCoilRecipe(pWriter, 2, ModItems.ALLOY_MAP.get("wrought-iron_rod").get(), ModItems.MATERIAL_MAP.get("aluminum_ingot").get());
        buildCoilRecipe(pWriter, 3, ModItems.ALLOY_MAP.get("steel_rod").get(), ModItems.MATERIAL_MAP.get("silver_ingot").get());
        buildCoilRecipe(pWriter, 4, ModItems.ALLOY_MAP.get("invar_rod").get(), ModItems.MATERIAL_MAP.get("zinc_ingot").get());
        buildCoilRecipe(pWriter, 5, ModItems.ALLOY_MAP.get("vanadium-steel_rod").get(), ModItems.ALLOY_MAP.get("nichrome_ingot").get());
        buildCoilRecipe(pWriter, 6, ModItems.ALLOY_MAP.get("tungsten-steel_rod").get(), ModItems.MATERIAL_MAP.get("platinum_ingot").get());
        buildCoilRecipe(pWriter, 7, ModItems.ALLOY_MAP.get("maraging-steel-1_rod").get(), ModItems.ALLOY_MAP.get("electrum_ingot").get());
        buildCoilRecipe(pWriter, 8, ModItems.ALLOY_MAP.get("maraging-steel-2_rod").get(), ModItems.MATERIAL_MAP.get("tantalum_ingot").get());
        buildCoilRecipe(pWriter, 9, ModItems.ALLOY_MAP.get("maraging-steel-3_rod").get(), ModItems.ALLOY_MAP.get("niobium-titanium_ingot").get());
        buildCoilRecipe(pWriter, 10, ModItems.ALLOY_MAP.get("samarium-cobalt_rod").get(), ModItems.ALLOY_MAP.get("niobium-titanium_ingot").get());
        buildCoilRecipe(pWriter, 11, ModItems.ALLOY_MAP.get("neodymium-iron-boron_rod").get(), ModItems.ALLOY_MAP.get("niobium-titanium_ingot").get());

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_MAP.get(12).get())
            .pattern("xRX")
            .pattern(" I ")
            .pattern("XRx")
            .define('X', ModItems.MATERIAL_MAP.get("iridium_ingot").get())
            .define('x', ModItems.MATERIAL_MAP.get("osmium_ingot").get())
            .define('R', Items.REDSTONE)
            .define('I', ModItems.ALLOY_MAP.get("terbium-dysprosium_rod").get())
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("iridium_ingot").get()), has(ModItems.MATERIAL_MAP.get("iridium_ingot").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("osmium_ingot").get()), has(ModItems.MATERIAL_MAP.get("osmium_ingot").get()))
            .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("terbium-dysprosium_rod").get()), has(ModItems.ALLOY_MAP.get("terbium-dysprosium_rod").get()))
            .save(pWriter);
    }

    private void buildCoilRecipe(Consumer<FinishedRecipe> pWriter, int tier, Item rod, Item ingots) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_MAP.get(tier).get())
            .pattern("XRX")
            .pattern(" I ")
            .pattern("XRX")
            .define('X', ingots)
            .define('R', Items.REDSTONE)
            .define('I', rod)
            .unlockedBy(getHasName(ingots), has(ingots))
            .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
            .unlockedBy(getHasName(rod), has(rod))
            .save(pWriter);
    }

    private void buildSolarPanelRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(1).get())
            .pattern("sss")
            .pattern("IFI")
            .pattern("IcI")
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', Items.IRON_INGOT)
            .define('F', ModBlocks.MACHINE_FRAME.get())
            .define('c', ModItems.COIL_MAP.get(1).get())
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
            .unlockedBy(getHasName(ModBlocks.MACHINE_FRAME.get()), has(ModBlocks.MACHINE_FRAME.get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(1).get()), has(ModItems.COIL_MAP.get(1).get()))
            .save(pWriter);

        List<Item> plates = List.of(
            ModItems.VANILLA_MAP.get("copper_plate").get()
        );
        for (int i = 0; i < plates.size(); i++) {
            int tier = i + 2;
            buildSolarPanelRecipe(pWriter, tier, plates.get(i));
        }
    }

    private void buildSolarPanelRecipe(Consumer<FinishedRecipe> pWriter, int tier, Item plate) {
        int coilTier = (tier - 1) / 6 + 1;

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("SsS")
            .pattern("PFP")
            .pattern("PcP")
            .define('S', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('P', plate)
            .define('F', ModBlocks.MACHINE_FRAME.get())
            .define('c', ModItems.COIL_MAP.get(coilTier).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(plate), has(plate))
            .unlockedBy(getHasName(ModBlocks.MACHINE_FRAME.get()), has(ModBlocks.MACHINE_FRAME.get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(coilTier).get()), has(ModItems.COIL_MAP.get(coilTier).get()))
            .save(pWriter);
    }

    private void buildBatteryRecipes(Consumer<FinishedRecipe> pWriter) {
        int tier = 1;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BATTERY_BLOCK_MAP.get(tier).get())
            .pattern("PLP")
            .pattern("LFL")
            .pattern("PcP")
            .define('P', ModItems.MATERIAL_MAP.get("antimony_plate").get())
            .define('L', ModItems.MATERIAL_MAP.get("lithium_rod").get())
            .define('F', ModBlocks.MACHINE_FRAME.get())
            .define('c', ModItems.COIL_MAP.get(tier).get())
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("antimony_plate").get()), has(ModItems.MATERIAL_MAP.get("antimony_plate").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("lithium_rod").get()), has(ModItems.MATERIAL_MAP.get("lithium_rod").get()))
            .unlockedBy(getHasName(ModBlocks.MACHINE_FRAME.get()), has(ModBlocks.MACHINE_FRAME.get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(tier).get()), has(ModItems.COIL_MAP.get(tier).get()))
            .save(pWriter);

        buildBatteryRecipe(pWriter, 2, ModItems.MATERIAL_MAP.get("tantalum_plate").get());
        buildBatteryRecipe(pWriter, 3, ModItems.MATERIAL_MAP.get("bismuth_plate").get());
        buildBatteryRecipe(pWriter, 4, ModItems.MATERIAL_MAP.get("cadmium_plate").get());
        buildBatteryRecipe(pWriter, 5, ModItems.MATERIAL_MAP.get("indium_plate").get());
        buildBatteryRecipe(pWriter, 6, ModItems.MATERIAL_MAP.get("palladium_plate").get());
        buildBatteryRecipe(pWriter, 7, ModItems.MATERIAL_MAP.get("rhodium_plate").get());
    }

    private static void buildBatteryRecipe(Consumer<FinishedRecipe> pWriter, int tier, Item plate) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BATTERY_BLOCK_MAP.get(tier).get())
            .pattern("PLP")
            .pattern("LBL")
            .pattern("PcP")
            .define('P', plate)
            .define('L', ModItems.MATERIAL_MAP.get("lithium_rod").get())
            .define('B', ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get())
            .define('c', ModItems.COIL_MAP.get(tier).get())
            .unlockedBy(getHasName(plate), has(plate))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("lithium_rod").get()), has(ModItems.MATERIAL_MAP.get("lithium_rod").get()))
            .unlockedBy(getHasName(ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(tier).get()), has(ModItems.COIL_MAP.get(tier).get()))
            .save(pWriter);
    }

    private void buildSackRecipes(Consumer<FinishedRecipe> pWriter) {
        int tier = 1;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SACK_MAP.get(tier).get())
            .pattern("SLS")
            .pattern("LsL")
            .pattern("LgL")
            .define('S', Items.STRING)
            .define('L', Items.LEATHER)
            .define('s', Items.CHEST)
            .define('g', ModItems.VANILLA_MAP.get("copper_gear").get())
            .unlockedBy(getHasName(Items.STRING), has(Items.STRING))
            .unlockedBy(getHasName(Items.LEATHER), has(Items.LEATHER))
            .unlockedBy(getHasName(Items.CHEST), has(Items.CHEST))
            .unlockedBy(getHasName(ModItems.VANILLA_MAP.get("copper_gear").get()), has(ModItems.VANILLA_MAP.get("copper_gear").get()))
            .save(pWriter);

        buildSackRecipe(pWriter, 2, Items.LEATHER, ModItems.VANILLA_MAP.get("diamond_gear").get());
        buildSackRecipe(pWriter, 3, Items.LEATHER, ModItems.ALLOY_MAP.get("wrought-iron_gear").get());
        buildSackRecipe(pWriter, 4, Items.LEATHER, ModItems.ALLOY_MAP.get("brass_gear").get());
        buildSackRecipe(pWriter, 5, ModItems.CUSTOM_ITEM_MAP.get("rubber").get(), ModItems.ALLOY_MAP.get("invar_gear").get());
        buildSackRecipe(pWriter, 6, ModItems.CUSTOM_ITEM_MAP.get("rubber").get(), ModItems.MATERIAL_MAP.get("zirconium_gear").get());
        buildSackRecipe(pWriter, 7, ModItems.CUSTOM_ITEM_MAP.get("rubber").get(), ModItems.MATERIAL_MAP.get("lead_gear").get());
        buildSackRecipe(pWriter, 8, ModItems.CUSTOM_ITEM_MAP.get("rubber").get(), ModItems.ALLOY_MAP.get("titanium-6al-2sn-4zr-2mo_gear").get());
    }

    private static void buildSackRecipe(Consumer<FinishedRecipe> pWriter, int tier, Item leather, Item gear) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SACK_MAP.get(tier).get())
            .pattern("SLS")
            .pattern("LsL")
            .pattern("LGL")
            .define('S', Items.STRING)
            .define('L', leather)
            .define('s', ModItems.SACK_MAP.get(tier - 1).get())
            .define('G', gear)
            .unlockedBy(getHasName(Items.STRING), has(Items.STRING))
            .unlockedBy(getHasName(leather), has(leather))
            .unlockedBy(getHasName(ModItems.SACK_MAP.get(tier - 1).get()), has(ModItems.SACK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(gear), has(gear))
            .save(pWriter);
    }
}