package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        buildBlockRecipe(pWriter, ModItems.STEEL_INGOT.get(), ModBlocks.STEEL_BLOCK.get());

        oreSmelting(pWriter, CLAY_SMELTABLES, RecipeCategory.MISC, Items.CLAY_BALL, 0.25f, 100, "clay");
        oreSmelting(pWriter, AL_SMELTABLES, RecipeCategory.MISC, ModItems.ALUMINUM_INGOT.get(), 0.25f, 100, "aluminum");
    }

    private static final List<ItemLike> CLAY_SMELTABLES = List.of(ModItems.CLAY_MINERAL_RAW.get());
    private static final List<ItemLike> AL_SMELTABLES = List.of(ModItems.BAUXITE.get(), ModItems.ALUMINUM_RAW.get());

    private void buildBlockRecipe(Consumer<FinishedRecipe> pWriter, Item item, Block block) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, block)
            .pattern("XXX")
            .pattern("XXX")
            .pattern("XXX")
            .define('X', item)
            .unlockedBy(getHasName(item), has(item))
            .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item, 9)
            .requires(block)
            .unlockedBy(getHasName(block), has(block))
            .save(pWriter);
    }
}
