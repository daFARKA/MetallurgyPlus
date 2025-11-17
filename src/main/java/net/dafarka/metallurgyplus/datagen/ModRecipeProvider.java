package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.Config;
import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.recipe.GrinderRecipe;
import net.dafarka.metallurgyplus.recipe.PressRecipe;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
        buildMaterialRecipes(pWriter);
        buildOreRecipes(pWriter);
        buildAlloyRecipes(pWriter);
        buildVanillaRecipes(pWriter);
        alloySmelterRecipeProvider.buildRecipes(pWriter);
        oreProcessingUnitRecipeProvider.buildRecipes(pWriter);
        grinderRecipeProvider.buildRecipes(pWriter);
        pressRecipeProvider.buildRecipes(pWriter);
        extractorRecipeProvider.buildRecipes(pWriter);
        buildCustomRecipes(pWriter);
        buildCableRecipes(pWriter);
        buildCoilRecipes(pWriter);
        buildSolarPanelRecipes(pWriter);
        buildBatteryRecipes(pWriter);
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
            .requires(ModItems.CUSTOM_ITEM_MAP.get("rare_earth1").get())
            .requires(ModItems.CUSTOM_ITEM_MAP.get("rare_earth2").get())
            .requires(ModItems.CUSTOM_ITEM_MAP.get("rare_earth3").get())
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("rare_earth1").get()), has(ModItems.CUSTOM_ITEM_MAP.get("rare_earth1").get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("rare_earth2").get()), has(ModItems.CUSTOM_ITEM_MAP.get("rare_earth2").get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("rare_earth3").get()), has(ModItems.CUSTOM_ITEM_MAP.get("rare_earth3").get()))
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get(), 1)
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
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('P', Items.PISTON)
            .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(Items.PISTON), has(Items.PISTON))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ORE_PROCESSING_UNIT.get())
            .pattern("IDI")
            .pattern("GFG")
            .pattern("ICI")
            .define('I', ModItems.VANILLA_MAP.get("iron_plate").get())
            .define('D', ModItems.VANILLA_MAP.get("diamond_gear").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('G', ModItems.VANILLA_MAP.get("iron_gear").get())
            .define('C', ModItems.VANILLA_MAP.get("copper_gear").get())
            .unlockedBy(getHasName(ModItems.VANILLA_MAP.get("iron_plate").get()), has(ModItems.VANILLA_MAP.get("iron_plate").get()))
            .unlockedBy(getHasName(ModItems.VANILLA_MAP.get("diamond_gear").get()), has(ModItems.VANILLA_MAP.get("diamond_gear").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.VANILLA_MAP.get("iron_gear").get()), has(ModItems.VANILLA_MAP.get("iron_gear").get()))
            .unlockedBy(getHasName(ModItems.VANILLA_MAP.get("copper_gear").get()), has(ModItems.VANILLA_MAP.get("copper_gear").get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.EXTRACTOR.get())
            .pattern("NCN")
            .pattern("GFG")
            .pattern("NSN")
            .define('N', ModItems.MATERIAL_MAP.get("nickel_plate").get())
            .define('C', ModItems.MATERIAL_MAP.get("chromium_rod").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('G', ModItems.MATERIAL_MAP.get("graphite_gear").get())
            .define('S', ModItems.MATERIAL_MAP.get("selenium_plate").get())
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("nickel_plate").get()), has(ModItems.MATERIAL_MAP.get("nickel_plate").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("chromium_rod").get()), has(ModItems.MATERIAL_MAP.get("chromium_rod").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
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
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('N', ModItems.MATERIAL_MAP.get("nickel_gear").get())
            .define('T', ModItems.MATERIAL_MAP.get("tin_gear").get())
            .define('A', ModItems.MATERIAL_MAP.get("aluminum_gear").get())
            .unlockedBy(getHasName(Items.BRICK), has(Items.BRICK))
            .unlockedBy(getHasName(ModItems.VANILLA_MAP.get("iron_plate").get()), has(ModItems.VANILLA_MAP.get("iron_plate").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("lead_gear").get()), has(ModItems.MATERIAL_MAP.get("lead_gear").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
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
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('S', ModItems.ALLOY_MAP.get("steel_gear").get())
            .define('s', ModItems.ALLOY_MAP.get("steel_plate").get())
            .unlockedBy(getHasName(Items.FLINT), has(Items.FLINT))
            .unlockedBy(getHasName(Items.PISTON), has(Items.PISTON))
            .unlockedBy(getHasName(ModBlocks.ALLOY_BLOCKS_MAP.get("zamak_block").get()), has(ModBlocks.ALLOY_BLOCKS_MAP.get("zamak_block").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
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
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('H', ModItems.MATERIAL_MAP.get("holmium_gear").get())
            .define('Y', ModItems.MATERIAL_MAP.get("ytterbium_gear").get())
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("mixed_rare_earth_alloy_plate").get()), has(ModItems.CUSTOM_ITEM_MAP.get("mixed_rare_earth_alloy_plate").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("promethium_rod").get()), has(ModItems.MATERIAL_MAP.get("promethium_rod").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("europium_gear").get()), has(ModItems.MATERIAL_MAP.get("europium_gear").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("holmium_gear").get()), has(ModItems.MATERIAL_MAP.get("holmium_gear").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("ytterbium_gear").get()), has(ModItems.MATERIAL_MAP.get("ytterbium_gear").get()))
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CABLE_BLOCKS_MAP.get(2).get())
            .pattern("XCX")
            .pattern("CcC")
            .pattern("XCX")
            .define('X', ModBlocks.CABLE_BLOCKS_MAP.get(1).get())
            .define('C', ModItems.ALLOY_MAP.get("cupronickel_plate").get())
            .define('c', ModItems.COIL_MAP.get(2).get())
            .unlockedBy(getHasName(ModBlocks.CABLE_BLOCKS_MAP.get(1).get()), has(ModBlocks.CABLE_BLOCKS_MAP.get(1).get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("cupronickel_plate").get()), has(ModItems.ALLOY_MAP.get("cupronickel_plate").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(2).get()), has(ModItems.COIL_MAP.get(2).get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CABLE_BLOCKS_MAP.get(3).get())
            .pattern("XCX")
            .pattern("CcC")
            .pattern("XCX")
            .define('X', ModBlocks.CABLE_BLOCKS_MAP.get(2).get())
            .define('C', ModItems.ALLOY_MAP.get("aluminum-magnesium_plate").get())
            .define('c', ModItems.COIL_MAP.get(3).get())
            .unlockedBy(getHasName(ModBlocks.CABLE_BLOCKS_MAP.get(2).get()), has(ModBlocks.CABLE_BLOCKS_MAP.get(2).get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("aluminum-magnesium_plate").get()), has(ModItems.ALLOY_MAP.get("aluminum-magnesium_plate").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(3).get()), has(ModItems.COIL_MAP.get(3).get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CABLE_BLOCKS_MAP.get(4).get())
            .pattern("XCX")
            .pattern("CcC")
            .pattern("XCX")
            .define('X', ModBlocks.CABLE_BLOCKS_MAP.get(3).get())
            .define('C', ModItems.MATERIAL_MAP.get("silver_plate").get())
            .define('c', ModItems.COIL_MAP.get(4).get())
            .unlockedBy(getHasName(ModBlocks.CABLE_BLOCKS_MAP.get(3).get()), has(ModBlocks.CABLE_BLOCKS_MAP.get(3).get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("silver_plate").get()), has(ModItems.MATERIAL_MAP.get("silver_plate").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(4).get()), has(ModItems.COIL_MAP.get(4).get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CABLE_BLOCKS_MAP.get(5).get())
            .pattern("XCX")
            .pattern("CcC")
            .pattern("XCX")
            .define('X', ModBlocks.CABLE_BLOCKS_MAP.get(4).get())
            .define('C', ModItems.ALLOY_MAP.get("electrum_plate").get())
            .define('c', ModItems.COIL_MAP.get(5).get())
            .unlockedBy(getHasName(ModBlocks.CABLE_BLOCKS_MAP.get(4).get()), has(ModBlocks.CABLE_BLOCKS_MAP.get(4).get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("electrum_plate").get()), has(ModItems.ALLOY_MAP.get("electrum_plate").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(5).get()), has(ModItems.COIL_MAP.get(5).get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CABLE_BLOCKS_MAP.get(6).get())
            .pattern("XCX")
            .pattern("CcC")
            .pattern("XCX")
            .define('X', ModBlocks.CABLE_BLOCKS_MAP.get(5).get())
            .define('C', ModItems.MATERIAL_MAP.get("palladium_plate").get())
            .define('c', ModItems.COIL_MAP.get(6).get())
            .unlockedBy(getHasName(ModBlocks.CABLE_BLOCKS_MAP.get(5).get()), has(ModBlocks.CABLE_BLOCKS_MAP.get(5).get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("palladium_plate").get()), has(ModItems.MATERIAL_MAP.get("palladium_plate").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(6).get()), has(ModItems.COIL_MAP.get(6).get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CABLE_BLOCKS_MAP.get(7).get())
            .pattern("XCX")
            .pattern("CcC")
            .pattern("XCX")
            .define('X', ModBlocks.CABLE_BLOCKS_MAP.get(6).get())
            .define('C', ModItems.MATERIAL_MAP.get("platinum_plate").get())
            .define('c', ModItems.COIL_MAP.get(7).get())
            .unlockedBy(getHasName(ModBlocks.CABLE_BLOCKS_MAP.get(6).get()), has(ModBlocks.CABLE_BLOCKS_MAP.get(6).get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("platinum_plate").get()), has(ModItems.MATERIAL_MAP.get("platinum_plate").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(7).get()), has(ModItems.COIL_MAP.get(7).get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CABLE_BLOCKS_MAP.get(8).get())
            .pattern("XCX")
            .pattern("CcC")
            .pattern("XCX")
            .define('X', ModBlocks.CABLE_BLOCKS_MAP.get(7).get())
            .define('C', ModItems.ALLOY_MAP.get("niobium-titanium_plate").get())
            .define('c', ModItems.COIL_MAP.get(8).get())
            .unlockedBy(getHasName(ModBlocks.CABLE_BLOCKS_MAP.get(7).get()), has(ModBlocks.CABLE_BLOCKS_MAP.get(7).get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("niobium-titanium_plate").get()), has(ModItems.ALLOY_MAP.get("niobium-titanium_plate").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(8).get()), has(ModItems.COIL_MAP.get(8).get()))
            .save(pWriter);
    }

    private void buildCoilRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_MAP.get(1).get())
            .pattern("XRX")
            .pattern(" I ")
            .pattern("XRX")
            .define('X', Items.COPPER_INGOT)
            .define('R', Items.REDSTONE)
            .define('I', ModItems.VANILLA_MAP.get("iron_rod").get())
            .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
            .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
            .unlockedBy(getHasName(ModItems.VANILLA_MAP.get("iron_rod").get()), has(ModItems.VANILLA_MAP.get("iron_rod").get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_MAP.get(2).get())
            .pattern("XRX")
            .pattern(" I ")
            .pattern("XRX")
            .define('X', ModItems.MATERIAL_MAP.get("tin_ingot").get())
            .define('R', Items.REDSTONE)
            .define('I', ModItems.ALLOY_MAP.get("steel_rod").get())
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("tin_ingot").get()), has(ModItems.MATERIAL_MAP.get("tin_ingot").get()))
            .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("steel_rod").get()), has(ModItems.ALLOY_MAP.get("steel_rod").get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_MAP.get(3).get())
            .pattern("XRX")
            .pattern(" I ")
            .pattern("XRX")
            .define('X', ModItems.ALLOY_MAP.get("bronze_ingot").get())
            .define('R', Items.REDSTONE)
            .define('I', ModItems.ALLOY_MAP.get("manganese-steel_rod").get())
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("bronze_ingot").get()), has(ModItems.ALLOY_MAP.get("bronze_ingot").get()))
            .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("manganese-steel_rod").get()), has(ModItems.ALLOY_MAP.get("manganese-steel_rod").get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_MAP.get(4).get())
            .pattern("XRX")
            .pattern(" I ")
            .pattern("XRX")
            .define('X', ModItems.MATERIAL_MAP.get("lead_ingot").get())
            .define('R', Items.REDSTONE)
            .define('I', ModItems.ALLOY_MAP.get("invar_rod").get())
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("lead_ingot").get()), has(ModItems.MATERIAL_MAP.get("lead_ingot").get()))
            .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("invar_rod").get()), has(ModItems.ALLOY_MAP.get("invar_rod").get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_MAP.get(5).get())
            .pattern("XRX")
            .pattern(" I ")
            .pattern("XRX")
            .define('X', ModItems.ALLOY_MAP.get("solder_ingot").get())
            .define('R', Items.REDSTONE)
            .define('I', ModItems.ALLOY_MAP.get("nitinol_rod").get())
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("solder_ingot").get()), has(ModItems.ALLOY_MAP.get("solder_ingot").get()))
            .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("nitinol_rod").get()), has(ModItems.ALLOY_MAP.get("nitinol_rod").get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_MAP.get(6).get())
            .pattern("XRX")
            .pattern(" I ")
            .pattern("XRX")
            .define('X', ModItems.ALLOY_MAP.get("aluminum-magnesium-zinc_ingot").get())
            .define('R', Items.REDSTONE)
            .define('I', ModItems.ALLOY_MAP.get("maraging-steel-1_rod").get())
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("aluminum-magnesium-zinc_ingot").get()), has(ModItems.ALLOY_MAP.get("aluminum-magnesium-zinc_ingot").get()))
            .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("maraging-steel-1_rod").get()), has(ModItems.ALLOY_MAP.get("maraging-steel-1_rod").get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_MAP.get(7).get())
            .pattern("XRX")
            .pattern(" I ")
            .pattern("XRX")
            .define('X', ModItems.ALLOY_MAP.get("aluminum-zirconium_ingot").get())
            .define('R', Items.REDSTONE)
            .define('I', ModItems.ALLOY_MAP.get("maraging-steel-2_rod").get())
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("aluminum-zirconium_ingot").get()), has(ModItems.ALLOY_MAP.get("aluminum-zirconium_ingot").get()))
            .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("maraging-steel-2_rod").get()), has(ModItems.ALLOY_MAP.get("maraging-steel-2_rod").get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_MAP.get(8).get())
            .pattern("xRX")
            .pattern(" I ")
            .pattern("XRx")
            .define('X', ModItems.MATERIAL_MAP.get("iridium_ingot").get())
            .define('x', ModItems.MATERIAL_MAP.get("osmium_ingot").get())
            .define('R', Items.REDSTONE)
            .define('I', ModItems.ALLOY_MAP.get("maraging-steel-3_rod").get())
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("iridium_ingot").get()), has(ModItems.MATERIAL_MAP.get("iridium_ingot").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("osmium_ingot").get()), has(ModItems.MATERIAL_MAP.get("osmium_ingot").get()))
            .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("maraging-steel-3_rod").get()), has(ModItems.ALLOY_MAP.get("maraging-steel-3_rod").get()))
            .save(pWriter);
    }

    private void buildSolarPanelRecipes(Consumer<FinishedRecipe> pWriter) {
        int tier = 1;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("sss")
            .pattern("IFI")
            .pattern("IcI")
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', Items.IRON_INGOT)
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(1).get())
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(1).get()), has(ModItems.COIL_MAP.get(1).get()))
            .save(pWriter);

        tier = 2;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', Items.COPPER_INGOT)
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(1).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(1).get()), has(ModItems.COIL_MAP.get(1).get()))
            .save(pWriter);

        tier = 3;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("bronze_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(1).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("bronze_plate").get()), has(ModItems.ALLOY_MAP.get("bronze_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(1).get()), has(ModItems.COIL_MAP.get(1).get()))
            .save(pWriter);

        tier = 4;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("brass_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(1).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("brass_plate").get()), has(ModItems.ALLOY_MAP.get("brass_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(1).get()), has(ModItems.COIL_MAP.get(1).get()))
            .save(pWriter);

        tier = 5;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("spring-copper_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(2).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("spring-copper_plate").get()), has(ModItems.ALLOY_MAP.get("spring-copper_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(2).get()), has(ModItems.COIL_MAP.get(2).get()))
            .save(pWriter);

        tier = 6;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("cupronickel_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(2).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("cupronickel_plate").get()), has(ModItems.ALLOY_MAP.get("cupronickel_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(2).get()), has(ModItems.COIL_MAP.get(2).get()))
            .save(pWriter);

        tier = 7;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.MATERIAL_MAP.get("graphite_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(2).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("graphite_plate").get()), has(ModItems.MATERIAL_MAP.get("graphite_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(2).get()), has(ModItems.COIL_MAP.get(2).get()))
            .save(pWriter);

        tier = 8;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("steel_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(2).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("steel_plate").get()), has(ModItems.ALLOY_MAP.get("steel_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(2).get()), has(ModItems.COIL_MAP.get(2).get()))
            .save(pWriter);

        tier = 9;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("wrought-iron_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(3).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("wrought-iron_plate").get()), has(ModItems.ALLOY_MAP.get("wrought-iron_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(3).get()), has(ModItems.COIL_MAP.get(3).get()))
            .save(pWriter);

        tier = 10;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("pig-iron_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(3).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("pig-iron_plate").get()), has(ModItems.ALLOY_MAP.get("pig-iron_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(3).get()), has(ModItems.COIL_MAP.get(3).get()))
            .save(pWriter);

        tier = 11;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("spring-steel_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(3).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("spring-steel_plate").get()), has(ModItems.ALLOY_MAP.get("spring-steel_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(3).get()), has(ModItems.COIL_MAP.get(3).get()))
            .save(pWriter);

        tier = 12;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("tungsten-steel_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(3).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("tungsten-steel_plate").get()), has(ModItems.ALLOY_MAP.get("tungsten-steel_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(3).get()), has(ModItems.COIL_MAP.get(3).get()))
            .save(pWriter);

        tier = 13;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("stainless-steel_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(4).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("stainless-steel_plate").get()), has(ModItems.ALLOY_MAP.get("stainless-steel_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(4).get()), has(ModItems.COIL_MAP.get(4).get()))
            .save(pWriter);

        tier = 14;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("invar_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(4).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("invar_plate").get()), has(ModItems.ALLOY_MAP.get("invar_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(4).get()), has(ModItems.COIL_MAP.get(4).get()))
            .save(pWriter);

        tier = 15;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("zamak_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(4).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("zamak_plate").get()), has(ModItems.ALLOY_MAP.get("zamak_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(4).get()), has(ModItems.COIL_MAP.get(4).get()))
            .save(pWriter);

        tier = 16;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("aluminum-scandium_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(4).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("aluminum-scandium_plate").get()), has(ModItems.ALLOY_MAP.get("aluminum-scandium_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(4).get()), has(ModItems.COIL_MAP.get(4).get()))
            .save(pWriter);

        tier = 17;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("aluminum-magnesium_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(5).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("aluminum-magnesium_plate").get()), has(ModItems.ALLOY_MAP.get("aluminum-magnesium_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(5).get()), has(ModItems.COIL_MAP.get(5).get()))
            .save(pWriter);

        tier = 18;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("aluminum-magnesium-zinc_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(5).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("aluminum-magnesium-zinc_plate").get()), has(ModItems.ALLOY_MAP.get("aluminum-magnesium-zinc_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(5).get()), has(ModItems.COIL_MAP.get(5).get()))
            .save(pWriter);

        tier = 19;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("aluminum-zirconium_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(5).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("aluminum-zirconium_plate").get()), has(ModItems.ALLOY_MAP.get("aluminum-zirconium_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(5).get()), has(ModItems.COIL_MAP.get(5).get()))
            .save(pWriter);

        tier = 20;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("nichrome_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(6).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("nichrome_plate").get()), has(ModItems.ALLOY_MAP.get("nichrome_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(6).get()), has(ModItems.COIL_MAP.get(6).get()))
            .save(pWriter);

        tier = 21;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("cobalt-chromium_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(6).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("cobalt-chromium_plate").get()), has(ModItems.ALLOY_MAP.get("cobalt-chromium_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(6).get()), has(ModItems.COIL_MAP.get(6).get()))
            .save(pWriter);

        tier = 22;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("titanium-6al-4v_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(6).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("titanium-6al-4v_plate").get()), has(ModItems.ALLOY_MAP.get("titanium-6al-4v_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(6).get()), has(ModItems.COIL_MAP.get(6).get()))
            .save(pWriter);

        tier = 23;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("titanium-6al-7nb_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(7).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("titanium-6al-7nb_plate").get()), has(ModItems.ALLOY_MAP.get("titanium-6al-7nb_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(7).get()), has(ModItems.COIL_MAP.get(7).get()))
            .save(pWriter);

        tier = 24;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("titanium-10v-2fe-3al_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(7).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("titanium-10v-2fe-3al_plate").get()), has(ModItems.ALLOY_MAP.get("titanium-10v-2fe-3al_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(7).get()), has(ModItems.COIL_MAP.get(7).get()))
            .save(pWriter);

        tier = 25;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("titanium-8al-1mo-1v_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(7).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("titanium-8al-1mo-1v_plate").get()), has(ModItems.ALLOY_MAP.get("titanium-8al-1mo-1v_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(7).get()), has(ModItems.COIL_MAP.get(7).get()))
            .save(pWriter);

        tier = 26;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier).get())
            .pattern("PsP")
            .pattern("IFI")
            .pattern("IcI")
            .define('P', ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get())
            .define('s', ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get())
            .define('I', ModItems.ALLOY_MAP.get("titanium-6al-2sn-4zr-2mo_plate").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(8).get())
            .unlockedBy(getHasName(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.SOLAR_PANEL_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()), has(ModItems.CUSTOM_ITEM_MAP.get("solar_cell").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("titanium-6al-2sn-4zr-2mo_plate").get()), has(ModItems.ALLOY_MAP.get("titanium-6al-2sn-4zr-2mo_plate").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(8).get()), has(ModItems.COIL_MAP.get(8).get()))
            .save(pWriter);
    }

    private void buildBatteryRecipes(Consumer<FinishedRecipe> pWriter) {
        int tier = 1;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BATTERY_BLOCK_MAP.get(tier).get())
            .pattern("ILI")
            .pattern("LFL")
            .pattern("IcI")
            .define('I', ModItems.MATERIAL_MAP.get("antimony_plate").get())
            .define('L', ModItems.MATERIAL_MAP.get("lithium_rod").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('c', ModItems.COIL_MAP.get(tier).get())
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("antimony_plate").get()), has(ModItems.MATERIAL_MAP.get("antimony_plate").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("lithium_rod").get()), has(ModItems.MATERIAL_MAP.get("lithium_rod").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(tier).get()), has(ModItems.COIL_MAP.get(tier).get()))
            .save(pWriter);

        tier = 2;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BATTERY_BLOCK_MAP.get(tier).get())
            .pattern("ILI")
            .pattern("LFL")
            .pattern("IcI")
            .define('I', ModItems.MATERIAL_MAP.get("tantalum_plate").get())
            .define('L', ModItems.MATERIAL_MAP.get("lithium_rod").get())
            .define('F', ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get())
            .define('c', ModItems.COIL_MAP.get(tier).get())
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("tantalum_plate").get()), has(ModItems.MATERIAL_MAP.get("tantalum_plate").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("lithium_rod").get()), has(ModItems.MATERIAL_MAP.get("lithium_rod").get()))
            .unlockedBy(getHasName(ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(tier).get()), has(ModItems.COIL_MAP.get(tier).get()))
            .save(pWriter);

        tier = 3;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BATTERY_BLOCK_MAP.get(tier).get())
            .pattern("ILI")
            .pattern("LFL")
            .pattern("IcI")
            .define('I', ModItems.MATERIAL_MAP.get("bismuth_plate").get())
            .define('L', ModItems.MATERIAL_MAP.get("lithium_rod").get())
            .define('F', ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get())
            .define('c', ModItems.COIL_MAP.get(tier).get())
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("bismuth_plate").get()), has(ModItems.MATERIAL_MAP.get("bismuth_plate").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("lithium_rod").get()), has(ModItems.MATERIAL_MAP.get("lithium_rod").get()))
            .unlockedBy(getHasName(ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(tier).get()), has(ModItems.COIL_MAP.get(tier).get()))
            .save(pWriter);

        tier = 4;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BATTERY_BLOCK_MAP.get(tier).get())
            .pattern("ILI")
            .pattern("LFL")
            .pattern("IcI")
            .define('I', ModItems.MATERIAL_MAP.get("cadmium_plate").get())
            .define('L', ModItems.MATERIAL_MAP.get("lithium_rod").get())
            .define('F', ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get())
            .define('c', ModItems.COIL_MAP.get(tier).get())
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("cadmium_plate").get()), has(ModItems.MATERIAL_MAP.get("cadmium_plate").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("lithium_rod").get()), has(ModItems.MATERIAL_MAP.get("lithium_rod").get()))
            .unlockedBy(getHasName(ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(tier).get()), has(ModItems.COIL_MAP.get(tier).get()))
            .save(pWriter);

        tier = 5;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BATTERY_BLOCK_MAP.get(tier).get())
            .pattern("ILI")
            .pattern("LFL")
            .pattern("IcI")
            .define('I', ModItems.MATERIAL_MAP.get("indium_plate").get())
            .define('L', ModItems.MATERIAL_MAP.get("lithium_rod").get())
            .define('F', ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get())
            .define('c', ModItems.COIL_MAP.get(tier).get())
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("indium_plate").get()), has(ModItems.MATERIAL_MAP.get("indium_plate").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("lithium_rod").get()), has(ModItems.MATERIAL_MAP.get("lithium_rod").get()))
            .unlockedBy(getHasName(ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(tier).get()), has(ModItems.COIL_MAP.get(tier).get()))
            .save(pWriter);

        tier = 6;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BATTERY_BLOCK_MAP.get(tier).get())
            .pattern("ILI")
            .pattern("LFL")
            .pattern("IcI")
            .define('I', ModItems.MATERIAL_MAP.get("palladium_plate").get())
            .define('L', ModItems.MATERIAL_MAP.get("lithium_rod").get())
            .define('F', ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get())
            .define('c', ModItems.COIL_MAP.get(tier).get())
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("palladium_plate").get()), has(ModItems.MATERIAL_MAP.get("palladium_plate").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("lithium_rod").get()), has(ModItems.MATERIAL_MAP.get("lithium_rod").get()))
            .unlockedBy(getHasName(ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(tier).get()), has(ModItems.COIL_MAP.get(tier).get()))
            .save(pWriter);

        tier = 7;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BATTERY_BLOCK_MAP.get(tier).get())
            .pattern("ILI")
            .pattern("LFL")
            .pattern("IcI")
            .define('I', ModItems.MATERIAL_MAP.get("rhodium_plate").get())
            .define('L', ModItems.MATERIAL_MAP.get("lithium_rod").get())
            .define('F', ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get())
            .define('c', ModItems.COIL_MAP.get(tier).get())
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("rhodium_plate").get()), has(ModItems.MATERIAL_MAP.get("rhodium_plate").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("lithium_rod").get()), has(ModItems.MATERIAL_MAP.get("lithium_rod").get()))
            .unlockedBy(getHasName(ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get()), has(ModBlocks.BATTERY_BLOCK_MAP.get(tier - 1).get()))
            .unlockedBy(getHasName(ModItems.COIL_MAP.get(tier).get()), has(ModItems.COIL_MAP.get(tier).get()))
            .save(pWriter);
    }
}