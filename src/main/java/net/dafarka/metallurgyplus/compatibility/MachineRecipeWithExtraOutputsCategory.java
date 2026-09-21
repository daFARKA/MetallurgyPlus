package net.dafarka.metallurgyplus.compatibility;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.dafarka.metallurgyplus.block.custom.MachineBlock;
import net.dafarka.metallurgyplus.recipe.MachineRecipeWithExtraOutputs;
import net.minecraft.resources.ResourceLocation;

public class MachineRecipeWithExtraOutputsCategory extends MachineRecipeCategoryBase<MachineRecipeWithExtraOutputs> {
    
    public MachineRecipeWithExtraOutputsCategory(IGuiHelper guiHelper, ResourceLocation uid, MachineBlock machineBlock) {
        super(guiHelper, uid, machineBlock, MachineRecipeWithExtraOutputs.class);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MachineRecipeWithExtraOutputs recipe, IFocusGroup focuses) {
        super.setRecipe(builder, recipe, focuses);

        if (recipe.getExtraOutputs() == null) {
            return;
        }

        int[][] outputPositions = machineBlock.getOutputPositions();

        for (int i = 0; i < recipe.getExtraOutputs().size(); i++) {
            int outputIndex = i + 1;

            if (outputIndex >= outputPositions.length) {
                break;
            }

            builder.addSlot(
                RecipeIngredientRole.OUTPUT,
                outputPositions[outputIndex][0] - OFFSET,
                outputPositions[outputIndex][1] - OFFSET
            ).addItemStack(recipe.getExtraOutputs().get(i));
        }
    }
}