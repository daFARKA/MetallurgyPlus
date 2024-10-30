package net.dafarka.metallurgyplus.datagen;

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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        buildMaterialRecipe(pWriter);

        oreSmelting(pWriter, CLAY_SMELTABLES, RecipeCategory.MISC, Items.CLAY_BALL, 0.25f, 100, "clay");
        //oreSmelting(pWriter, AL_SMELTABLES, RecipeCategory.MISC, ModItems.MATERIAL_MAP.get("aluminum_ingot").get(), 0.25f, 100, "aluminum");
    }

    private static final List<ItemLike> CLAY_SMELTABLES = List.of(ModItems.CLAY_MINERAL_RAW.get());
    //private static final List<ItemLike> AL_SMELTABLES = List.of(ModItems.BAUXITE.get(), ModItems.MATERIAL_MAP.get("aluminum_raw").get());

    private void buildMaterialRecipe(Consumer<FinishedRecipe> pWriter) {
        List<String> oldMaterials = new ArrayList<>();
        for (RegistryObject<Item> item : ModItems.MATERIAL_MAP.values()){
            String currentName = item.getId().getPath();
            String currentMaterialName = currentName.split("_")[0];
            if (!oldMaterials.contains(currentMaterialName)) {
                oldMaterials.add(currentMaterialName);

                Item ingot = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[0]).get();
                Item dust = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[1]).get();
                Item gear = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[2]).get();
                Item nugget = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[3]).get();
                Item plate = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[4]).get();
                Item rod = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[5]).get();
                Item raw = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[6]).get();

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

                oreSmelting(pWriter, List.of(raw), RecipeCategory.MISC, ingot, 0.25f, 100, "aluminum");
            }
        }

    }
}
