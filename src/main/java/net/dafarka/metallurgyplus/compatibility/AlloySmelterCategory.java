package net.dafarka.metallurgyplus.compatibility;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.recipe.AlloySmelterRecipe;
import net.dafarka.metallurgyplus.screen.menu.AlloySmelterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class AlloySmelterCategory implements IRecipeCategory<AlloySmelterRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(MetallurgyPlus.MODID, "alloy_smelter");
    public static final ResourceLocation TEXTURE = new ResourceLocation(MetallurgyPlus.MODID, "textures/gui/alloy_smelter_gui.png");

    public static final RecipeType<AlloySmelterRecipe> ALLOY_SMELTER_TYPE = new RecipeType<>(UID, AlloySmelterRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    private static final int OFFSET = 4;

    public AlloySmelterCategory(IGuiHelper iGuiHelper) {
        this.background = iGuiHelper.createDrawable(TEXTURE, OFFSET, OFFSET, 169, 77);
        this.icon = iGuiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ALLOY_SMELTER.get()));
    }

    @Override
    public RecipeType<AlloySmelterRecipe> getRecipeType() {
        return ALLOY_SMELTER_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.metallurgyplus.alloy_smelter");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlloySmelterRecipe recipe, IFocusGroup focuses) {
        int i = 0;
        for (Ingredient ingredient : recipe.getIngredients()) {
            builder.addSlot(RecipeIngredientRole.INPUT, AlloySmelterMenu.INPUT_POSITIONS[i][0] - OFFSET, AlloySmelterMenu.INPUT_POSITIONS[i][1] - OFFSET)
                .addItemStack(new ItemStack(ingredient.getItems()[0].getItem(), recipe.getInputAmountForIngredient(ingredient)));
            i++;
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 116 - OFFSET, 18 - OFFSET).addItemStack(recipe.getResultItem(null));
    }
}
