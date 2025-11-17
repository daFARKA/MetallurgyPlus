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
import net.dafarka.metallurgyplus.recipe.GrinderRecipe;
import net.dafarka.metallurgyplus.screen.menu.GrinderMenu;
import net.dafarka.metallurgyplus.screen.menu.OreProcessingUnitMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class GrinderCategory implements IRecipeCategory<GrinderRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(MetallurgyPlus.MODID, "grinder");
    public static final ResourceLocation TEXTURE = new ResourceLocation(MetallurgyPlus.MODID, "textures/gui/grinder_gui.png");

    public static final RecipeType<GrinderRecipe> GRINDER_TYPE = new RecipeType<>(UID, GrinderRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    private static final int OFFSET = 4;

    public GrinderCategory(IGuiHelper iGuiHelper) {
        this.background = iGuiHelper.createDrawable(TEXTURE, OFFSET, OFFSET, 169, 77);
        this.icon = iGuiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.GRINDER.get()));
    }

    @Override
    public RecipeType<GrinderRecipe> getRecipeType() {
        return GRINDER_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.metallurgyplus.grinder");
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
    public void setRecipe(IRecipeLayoutBuilder builder, GrinderRecipe recipe, IFocusGroup focuses) {
        int i = 0;
        for (Ingredient ingredient : recipe.getIngredients()) {
            builder.addSlot(RecipeIngredientRole.INPUT, GrinderMenu.INPUT_POSITIONS[i][0] - OFFSET, GrinderMenu.INPUT_POSITIONS[i][1] - OFFSET)
                .addItemStack(new ItemStack(ingredient.getItems()[0].getItem(), recipe.getInputAmountForIngredient(ingredient)));
            i++;
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 80 - OFFSET, 21 - OFFSET).addItemStack(recipe.getResultItem(null));
        NonNullList<ItemStack> extraOutputs = recipe.getExtraOutputs();
        NonNullList<Double> extraOutputChances = recipe.getExtraOutputChances();
        if (extraOutputs != null) {
            i = 1;
            for (ItemStack extraOutput : extraOutputs) {
                int j = i - 1;
                builder.addSlot(RecipeIngredientRole.OUTPUT, GrinderMenu.OUTPUT_POSITIONS[i][0] - OFFSET, GrinderMenu.OUTPUT_POSITIONS[i][1] - OFFSET)
                    .addItemStack(extraOutput)
                    .addTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.literal("Chance: " + (extraOutputChances.get(j) * 100) + "%").withStyle(
                        ChatFormatting.ITALIC)));
                i++;
            }
        }
    }
}
