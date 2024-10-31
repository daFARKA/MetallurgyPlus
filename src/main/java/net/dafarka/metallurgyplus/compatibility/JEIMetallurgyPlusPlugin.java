package net.dafarka.metallurgyplus.compatibility;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.recipe.AlloySmelterRecipe;
import net.dafarka.metallurgyplus.recipe.OreProcessingUnitRecipe;
import net.dafarka.metallurgyplus.screen.AlloySmelterScreen;
import net.dafarka.metallurgyplus.screen.OreProcessingUnitScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEIMetallurgyPlusPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(MetallurgyPlus.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AlloySmelterCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new OreProcessingUnitCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<AlloySmelterRecipe> alloySmelterRecipes = recipeManager.getAllRecipesFor(AlloySmelterRecipe.Type.INSTANCE);
        List<OreProcessingUnitRecipe> oreProcessingUnitRecipes = recipeManager.getAllRecipesFor(OreProcessingUnitRecipe.Type.INSTANCE);
        registration.addRecipes(AlloySmelterCategory.ALLOY_SMELTER_TYPE, alloySmelterRecipes);
        registration.addRecipes(OreProcessingUnitCategory.ORE_PROCESSING_UNIT_TYPE, oreProcessingUnitRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(AlloySmelterScreen.class, 87, 39, 30, 20,
            AlloySmelterCategory.ALLOY_SMELTER_TYPE);
        registration.addRecipeClickArea(OreProcessingUnitScreen.class, 32, 43, 30, 20,
            OreProcessingUnitCategory.ORE_PROCESSING_UNIT_TYPE);
    }
}
