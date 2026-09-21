package net.dafarka.metallurgyplus.compatibility;

import mezz.jei.api.helpers.IGuiHelper;
import net.dafarka.metallurgyplus.block.custom.MachineBlock;
import net.dafarka.metallurgyplus.recipe.MachineRecipe;
import net.minecraft.resources.ResourceLocation;

public class MachineRecipeCategory
    extends MachineRecipeCategoryBase<MachineRecipe> {

    public MachineRecipeCategory(IGuiHelper guiHelper, ResourceLocation uid, MachineBlock machineBlock) {
        super(guiHelper, uid, machineBlock, MachineRecipe.class);
    }
}