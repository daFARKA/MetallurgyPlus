package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.Config;
import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.item.ModItems;
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

    private static final List<ItemLike> CLAY_SMELTABLES = List.of(ModItems.CLAY_MINERAL_RAW.get());

    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
        alloySmelterRecipeProvider = new AlloySmelterRecipeProvider(pOutput);
        oreProcessingUnitRecipeProvider = new OreProcessingUnitRecipeProvider(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        buildMaterialRecipes(pWriter);
        buildOreRecipes(pWriter);
        buildAlloyRecipes(pWriter);
        alloySmelterRecipeProvider.buildRecipes(pWriter);
        oreProcessingUnitRecipeProvider.buildRecipes(pWriter);
        buildCustomRecipes(pWriter);

        if (Config.buildBlockEntitiesRecipies) {
            buildBlockEntitiesRecipes(pWriter);
        }
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

    private void buildCustomRecipes(Consumer<FinishedRecipe> pWriter) {
        oreSmelting(pWriter, CLAY_SMELTABLES, RecipeCategory.MISC, Items.CLAY_BALL, 0.25f, 100, "clay");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SMALL_RARE_EARTH.get(), 1)
            .requires(ModItems.RARE_EARTH1.get())
            .requires(ModItems.RARE_EARTH2.get())
            .requires(ModItems.RARE_EARTH3.get())
            .unlockedBy(getHasName(ModItems.RARE_EARTH1.get()), has(ModItems.RARE_EARTH1.get()))
            .unlockedBy(getHasName(ModItems.RARE_EARTH2.get()), has(ModItems.RARE_EARTH2.get()))
            .unlockedBy(getHasName(ModItems.RARE_EARTH3.get()), has(ModItems.RARE_EARTH3.get()))
            .save(pWriter);
    }

    private void buildBlockEntitiesRecipes(Consumer<FinishedRecipe> pWriter) {
        // These recipes need to be changed...
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ORE_PROCESSING_UNIT.get())
            .pattern("III")
            .pattern("IBI")
            .pattern("IPI")
            .define('I', Items.IRON_INGOT)
            .define('B', Items.IRON_BLOCK)
            .define('P', Items.PISTON)
            .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
            .unlockedBy(getHasName(Items.IRON_BLOCK), has(Items.IRON_BLOCK))
            .unlockedBy(getHasName(Items.PISTON), has(Items.PISTON))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ALLOY_SMELTER.get())
            .pattern("III")
            .pattern("IBI")
            .pattern("ICI")
            .define('I', Items.IRON_INGOT)
            .define('B', Items.IRON_BLOCK)
            .define('C', Items.COAL_BLOCK)
            .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
            .unlockedBy(getHasName(Items.IRON_BLOCK), has(Items.IRON_BLOCK))
            .unlockedBy(getHasName(Items.COAL_BLOCK), has(Items.COAL_BLOCK))
            .save(pWriter);
    }
}