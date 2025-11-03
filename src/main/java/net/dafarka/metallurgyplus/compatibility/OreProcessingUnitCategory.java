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
import net.dafarka.metallurgyplus.recipe.OreProcessingUnitRecipe;
import net.dafarka.metallurgyplus.screen.OreProcessingUnitMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class OreProcessingUnitCategory implements IRecipeCategory<OreProcessingUnitRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(MetallurgyPlus.MODID, "ore_processing_unit");
    public static final ResourceLocation TEXTURE = new ResourceLocation(MetallurgyPlus.MODID, "textures/gui/ore_processing_unit_gui.png");

    public static final RecipeType<OreProcessingUnitRecipe> ORE_PROCESSING_UNIT_TYPE = new RecipeType<>(UID, OreProcessingUnitRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    private static final int OFFSET = 4;

    public OreProcessingUnitCategory(IGuiHelper iGuiHelper) {
        this.background = iGuiHelper.createDrawable(TEXTURE, OFFSET, OFFSET, 169, 77);
        this.icon = iGuiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ORE_PROCESSING_UNIT.get()));
    }

    @Override
    public RecipeType<OreProcessingUnitRecipe> getRecipeType() {
        return ORE_PROCESSING_UNIT_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.metallurgyplus.ore_processing_unit");
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
    public void setRecipe(IRecipeLayoutBuilder builder, OreProcessingUnitRecipe recipe, IFocusGroup focuses) {
        // In an OPU there is only 1 input slot.
        Ingredient ingredient = recipe.getIngredients().get(0);

        builder.addSlot(RecipeIngredientRole.INPUT, OreProcessingUnitMenu.INPUT_POSITION[0] - OFFSET, OreProcessingUnitMenu.INPUT_POSITION[1] - OFFSET)
            .addItemStack(new ItemStack(ingredient.getItems()[0].getItem(), recipe.getInputAmountForIngredient(ingredient)));

        builder.addSlot(RecipeIngredientRole.OUTPUT, OreProcessingUnitMenu.OUTPUT_POSITIONS[0][0] - OFFSET, OreProcessingUnitMenu.OUTPUT_POSITIONS[0][1] - OFFSET).addItemStack(recipe.getResultItem(null));

        NonNullList<ItemStack> extraOutputs = recipe.getExtraOutputs();
        NonNullList<Double> extraOutputChances = recipe.getExtraOutputChances();
        if (extraOutputs != null) {
            int i = 1;
            for (ItemStack extraOutput : extraOutputs) {
                int j = i - 1;
                builder.addSlot(RecipeIngredientRole.OUTPUT, OreProcessingUnitMenu.OUTPUT_POSITIONS[i][0] - OFFSET, OreProcessingUnitMenu.OUTPUT_POSITIONS[i][1] - OFFSET)
                    .addItemStack(extraOutput)
                    .addTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.literal("Chance: " + (extraOutputChances.get(j) * 100) + "%")));
                i++;
            }
        }
    }
}
