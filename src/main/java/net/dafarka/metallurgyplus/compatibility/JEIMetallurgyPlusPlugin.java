package net.dafarka.metallurgyplus.compatibility;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.recipe.AlloySmelterRecipe;
import net.dafarka.metallurgyplus.recipe.GrinderRecipe;
import net.dafarka.metallurgyplus.recipe.OreProcessingUnitRecipe;
import net.dafarka.metallurgyplus.recipe.PressRecipe;
import net.dafarka.metallurgyplus.screen.AlloySmelterScreen;
import net.dafarka.metallurgyplus.screen.GrinderScreen;
import net.dafarka.metallurgyplus.screen.OreProcessingUnitScreen;
import net.dafarka.metallurgyplus.screen.PressScreen;
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
        registration.addRecipeCategories(new GrinderCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new PressCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<AlloySmelterRecipe> alloySmelterRecipes = recipeManager.getAllRecipesFor(AlloySmelterRecipe.Type.INSTANCE);
        List<OreProcessingUnitRecipe> oreProcessingUnitRecipes = recipeManager.getAllRecipesFor(OreProcessingUnitRecipe.Type.INSTANCE);
        List<GrinderRecipe> grinderRecipes = recipeManager.getAllRecipesFor(GrinderRecipe.Type.INSTANCE);
        List<PressRecipe> pressRecipes = recipeManager.getAllRecipesFor(PressRecipe.Type.INSTANCE);
        registration.addRecipes(AlloySmelterCategory.ALLOY_SMELTER_TYPE, alloySmelterRecipes);
        registration.addRecipes(OreProcessingUnitCategory.ORE_PROCESSING_UNIT_TYPE, oreProcessingUnitRecipes);
        registration.addRecipes(GrinderCategory.GRINDER_TYPE, grinderRecipes);
        registration.addRecipes(PressCategory.PRESS_TYPE, pressRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(AlloySmelterScreen.class, 87, 39, 30, 20,
            AlloySmelterCategory.ALLOY_SMELTER_TYPE);
        registration.addRecipeClickArea(OreProcessingUnitScreen.class, 32, 43, 30, 20,
            OreProcessingUnitCategory.ORE_PROCESSING_UNIT_TYPE);
        registration.addRecipeClickArea(GrinderScreen.class, 48, 34, 30, 20,
            GrinderCategory.GRINDER_TYPE);
        registration.addRecipeClickArea(PressScreen.class, 64, 38, 30, 20,
            PressCategory.PRESS_TYPE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
            new net.minecraft.world.item.ItemStack(ModBlocks.ALLOY_SMELTER.get()),
            AlloySmelterCategory.ALLOY_SMELTER_TYPE
        );

        registration.addRecipeCatalyst(
            new net.minecraft.world.item.ItemStack(ModBlocks.ORE_PROCESSING_UNIT.get()),
            OreProcessingUnitCategory.ORE_PROCESSING_UNIT_TYPE
        );

        registration.addRecipeCatalyst(
            new net.minecraft.world.item.ItemStack(ModBlocks.GRINDER.get()),
            GrinderCategory.GRINDER_TYPE
        );

        registration.addRecipeCatalyst(
            new net.minecraft.world.item.ItemStack(ModBlocks.PRESS.get()),
            PressCategory.PRESS_TYPE
        );
    }
}
