package net.dafarka.metallurgyplus.compatibility;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.recipe.MachineRecipe;
import net.dafarka.metallurgyplus.recipe.MachineRecipeWithExtraOutputs;
import net.dafarka.metallurgyplus.recipe.ModRecipeTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEIMetallurgyPlusPlugin implements IModPlugin {

    private MachineRecipeCategory alloySmelterCategory;
    private MachineRecipeWithExtraOutputsCategory oreProcessingUnitCategory;
    private MachineRecipeWithExtraOutputsCategory grinderCategory;
    private MachineRecipeCategory pressCategory;
    private MachineRecipeWithExtraOutputsCategory extractorCategory;
    private MachineRecipeWithExtraOutputsCategory gemstoneCutterCategory;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(MetallurgyPlus.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();

        alloySmelterCategory = new MachineRecipeCategory(
            guiHelper,
            new ResourceLocation(MetallurgyPlus.MODID, "alloy_smelter"),
            ModBlocks.ALLOY_SMELTER.get()
        );

        oreProcessingUnitCategory = new MachineRecipeWithExtraOutputsCategory(
            guiHelper,
            new ResourceLocation(MetallurgyPlus.MODID, "ore_processing_unit"),
            ModBlocks.ORE_PROCESSING_UNIT.get()
        );

        grinderCategory = new MachineRecipeWithExtraOutputsCategory(
            guiHelper,
            new ResourceLocation(MetallurgyPlus.MODID, "grinder"),
            ModBlocks.GRINDER.get()
        );

        pressCategory = new MachineRecipeCategory(
            guiHelper,
            new ResourceLocation(MetallurgyPlus.MODID, "press"),
            ModBlocks.PRESS.get()
        );

        extractorCategory = new MachineRecipeWithExtraOutputsCategory(
            guiHelper,
            new ResourceLocation(MetallurgyPlus.MODID, "extractor"),
            ModBlocks.EXTRACTOR.get()
        );

        gemstoneCutterCategory = new MachineRecipeWithExtraOutputsCategory(
            guiHelper,
            new ResourceLocation(MetallurgyPlus.MODID, "gemstone_cutter"),
            ModBlocks.GEMSTONE_CUTTER.get()
        );

        registration.addRecipeCategories(
            alloySmelterCategory,
            oreProcessingUnitCategory,
            grinderCategory,
            pressCategory,
            extractorCategory,
            gemstoneCutterCategory
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<MachineRecipe> alloySmelterRecipes = recipeManager.getAllRecipesFor(ModRecipeTypes.ALLOY_SMELTER_TYPE);
        List<MachineRecipeWithExtraOutputs> oreProcessingUnitRecipes = recipeManager.getAllRecipesFor(ModRecipeTypes.ORE_PROCESSING_UNIT_TYPE);
        List<MachineRecipeWithExtraOutputs> grinderRecipes = recipeManager.getAllRecipesFor(ModRecipeTypes.GRINDER_TYPE);
        List<MachineRecipe> pressRecipes = recipeManager.getAllRecipesFor(ModRecipeTypes.PRESS_TYPE);
        List<MachineRecipeWithExtraOutputs> extractorRecipes = recipeManager.getAllRecipesFor(ModRecipeTypes.EXTRACTOR_TYPE);
        List<MachineRecipeWithExtraOutputs> gemstoneCutterRecipes = recipeManager.getAllRecipesFor(ModRecipeTypes.GEMSTONE_CUTTER_TYPE);

        registration.addRecipes(alloySmelterCategory.getRecipeType(), alloySmelterRecipes);
        registration.addRecipes(oreProcessingUnitCategory.getRecipeType(), oreProcessingUnitRecipes);
        registration.addRecipes(grinderCategory.getRecipeType(), grinderRecipes);
        registration.addRecipes(pressCategory.getRecipeType(), pressRecipes);
        registration.addRecipes(extractorCategory.getRecipeType(), extractorRecipes);
        registration.addRecipes(gemstoneCutterCategory.getRecipeType(), gemstoneCutterRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {

    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.ALLOY_SMELTER.get()),
            alloySmelterCategory.getRecipeType()
        );

        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.ORE_PROCESSING_UNIT.get()),
            oreProcessingUnitCategory.getRecipeType()
        );

        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.GRINDER.get()),
            grinderCategory.getRecipeType()
        );

        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.PRESS.get()),
            pressCategory.getRecipeType()
        );

        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.EXTRACTOR.get()),
            extractorCategory.getRecipeType()
        );

        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.GEMSTONE_CUTTER.get()),
            gemstoneCutterCategory.getRecipeType()
        );
    }
}
