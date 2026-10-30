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
import net.dafarka.metallurgyplus.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ModularToolRecipeCategory implements IRecipeCategory<ModularToolJeiRecipe> {
    private static final ResourceLocation UID = new ResourceLocation(MetallurgyPlus.MODID, "modular_pickaxe");
    private static final ResourceLocation CRAFTING_TABLE_TEXTURE =
        new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");

    private final RecipeType<ModularToolJeiRecipe> recipeType =
        new RecipeType<>(UID, ModularToolJeiRecipe.class);
    private final IDrawable background;
    private final IDrawable icon;

    public ModularToolRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(CRAFTING_TABLE_TEXTURE, 29, 16, 114, 56);
        this.icon = guiHelper.createDrawableIngredient(
            VanillaTypes.ITEM_STACK,
            new ItemStack(ModItems.MODULAR_PICKAXE.get())
        );
    }

    @Override
    public RecipeType<ModularToolJeiRecipe> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Modular Pickaxe");
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
    public void setRecipe(IRecipeLayoutBuilder builder, ModularToolJeiRecipe recipe, IFocusGroup focuses) {
        for (int slot = 0; slot < 9; slot++) {
            ItemStack input = recipe.getInput(slot);
            if (input.isEmpty()) continue;

            builder.addSlot(
                RecipeIngredientRole.INPUT,
                1 + slot % 3 * 18,
                1 + slot / 3 * 18
            ).addItemStack(input);
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 19)
            .addItemStack(recipe.getOutput());
    }
}
