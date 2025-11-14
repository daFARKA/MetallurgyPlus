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
import net.dafarka.metallurgyplus.recipe.PressRecipe;
import net.dafarka.metallurgyplus.screen.menu.PressMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class PressCategory implements IRecipeCategory<PressRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(MetallurgyPlus.MODID, "press");
    public static final ResourceLocation TEXTURE = new ResourceLocation(MetallurgyPlus.MODID, "textures/gui/press_gui.png");

    public static final RecipeType<PressRecipe> PRESS_TYPE = new RecipeType<>(UID, PressRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    private static final int OFFSET = 4;

    public PressCategory(IGuiHelper iGuiHelper) {
        this.background = iGuiHelper.createDrawable(TEXTURE, OFFSET, OFFSET, 169, 77);
        this.icon = iGuiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.PRESS.get()));
    }

    @Override
    public RecipeType<PressRecipe> getRecipeType() {
        return PRESS_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.metallurgyplus.press");
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
    public void setRecipe(IRecipeLayoutBuilder builder, PressRecipe recipe, IFocusGroup focuses) {
        Ingredient ingredient = recipe.getIngredients().get(0);

        builder.addSlot(RecipeIngredientRole.INPUT, PressMenu.INPUT_POSITION[0] - OFFSET, PressMenu.INPUT_POSITION[1] - OFFSET)
            .addItemStack(new ItemStack(ingredient.getItems()[0].getItem(), recipe.getInputAmountForIngredient(ingredient)));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 98 - OFFSET, 17 - OFFSET).addItemStack(recipe.getResultItem(null));
    }
}
