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
import net.dafarka.metallurgyplus.block.custom.MachineBlock;
import net.dafarka.metallurgyplus.recipe.MachineRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class MachineRecipeCategoryBase<T extends MachineRecipe> implements IRecipeCategory<T> {

    protected static final int OFFSET = 4;

    protected final RecipeType<T> recipeType;
    protected final IDrawable background;
    protected final IDrawable icon;
    protected final Component title;
    protected final MachineBlock machineBlock;

    public MachineRecipeCategoryBase(IGuiHelper guiHelper, ResourceLocation uid, MachineBlock machineBlock, Class<T> recipeClass) {
        this.machineBlock = machineBlock;

        ResourceLocation texture = new ResourceLocation(MetallurgyPlus.MODID, "textures/gui/" + uid.getPath() + "_gui.png");

        this.recipeType = new RecipeType<>(uid, recipeClass);

        this.background = guiHelper.createDrawable(
            texture,
            OFFSET,
            OFFSET,
            169,
            77
        );

        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(machineBlock));

        this.title = Component.translatable(machineBlock.getDescriptionId());
    }

    @Override
    public RecipeType<T> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        int[][] inputPositions = machineBlock.getInputPositions();

        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            Ingredient ingredient = recipe.getIngredients().get(i);

            builder.addSlot(
                RecipeIngredientRole.INPUT,
                inputPositions[i][0] - OFFSET,
                inputPositions[i][1] - OFFSET
            ).addItemStack(
                new ItemStack(
                    ingredient.getItems()[0].getItem(),
                    recipe.getInputAmountForIngredient(ingredient)
                )
            );
        }

        int[][] outputPositions = machineBlock.getOutputPositions();

        builder.addSlot(
            RecipeIngredientRole.OUTPUT,
            outputPositions[0][0] - OFFSET,
            outputPositions[0][1] - OFFSET
        ).addItemStack(
            recipe.getResultItem(null)
        );
    }
}