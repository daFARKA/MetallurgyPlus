package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.Config;
import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.recipe.GrinderRecipe;
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

    private static final List<ItemLike> CLAY_SMELTABLES = List.of(ModItems.CUSTOM_ITEM_MAP.get("clay_mineral_raw").get());

    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
        alloySmelterRecipeProvider = new AlloySmelterRecipeProvider(pOutput);
        oreProcessingUnitRecipeProvider = new OreProcessingUnitRecipeProvider(pOutput);
        grinderRecipeProvider = new GrinderRecipeProvider(pOutput);
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
        buildCustomRecipes(pWriter);

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

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, rod)
                    .pattern("   ")
                    .pattern("  X")
                    .pattern(" X ")
                    .define('X', ingot)
                    .unlockedBy(getHasName(ingot), has(ingot))
                    .save(pWriter);

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
    }

    private void buildBlockEntitiesRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ORE_PROCESSING_UNIT.get())
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ALLOY_SMELTER.get())
            .pattern("BBB")
            .pattern("LFN")
            .pattern("TIA")
            .define('B', Items.BRICK)
            .define('I', Items.IRON_INGOT)
            .define('L', ModItems.MATERIAL_MAP.get("lead_gear").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('N', ModItems.MATERIAL_MAP.get("nickel_gear").get())
            .define('T', ModItems.MATERIAL_MAP.get("tin_gear").get())
            .define('A', ModItems.MATERIAL_MAP.get("aluminum_gear").get())
            .unlockedBy(getHasName(Items.BRICK), has(Items.BRICK))
            .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("lead_gear").get()), has(ModItems.MATERIAL_MAP.get("lead_gear").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("nickel_gear").get()), has(ModItems.MATERIAL_MAP.get("nickel_gear").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("tin_gear").get()), has(ModItems.MATERIAL_MAP.get("tin_gear").get()))
            .unlockedBy(getHasName(ModItems.MATERIAL_MAP.get("aluminum_gear").get()), has(ModItems.MATERIAL_MAP.get("aluminum_gear").get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.GRINDER.get())
            .pattern("fPf")
            .pattern("ZFZ")
            .pattern("SsS")
            .define('f', Items.FLINT)
            .define('P', Items.PISTON)
            .define('Z', ModBlocks.ALLOY_BLOCKS_MAP.get("zamak_block").get())
            .define('F', ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get())
            .define('S', ModItems.ALLOY_MAP.get("steel_gear").get())
            .define('s', ModItems.ALLOY_MAP.get("steel_ingot").get())
            .unlockedBy(getHasName(Items.FLINT), has(Items.FLINT))
            .unlockedBy(getHasName(Items.PISTON), has(Items.PISTON))
            .unlockedBy(getHasName(ModBlocks.ALLOY_BLOCKS_MAP.get("zamak_block").get()), has(ModBlocks.ALLOY_BLOCKS_MAP.get("zamak_block").get()))
            .unlockedBy(getHasName(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()), has(ModBlocks.CUSTOM_BLOCKS_MAP.get("machine_frame").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("steel_gear").get()), has(ModItems.ALLOY_MAP.get("steel_gear").get()))
            .unlockedBy(getHasName(ModItems.ALLOY_MAP.get("steel_ingot").get()), has(ModItems.ALLOY_MAP.get("steel_ingot").get()))
            .save(pWriter);
    }
}