package net.dafarka.metallurgyplus.compatibility;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.item.tool.ModularPickaxeItem;
import net.dafarka.metallurgyplus.item.tool.ModularTool;
import net.dafarka.metallurgyplus.recipe.MachineRecipe;
import net.dafarka.metallurgyplus.recipe.MachineRecipeWithExtraOutputs;
import net.dafarka.metallurgyplus.recipe.ModRecipeTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JeiPlugin
public class JEIMetallurgyPlusPlugin implements IModPlugin {

    private MachineRecipeCategory alloySmelterCategory;
    private MachineRecipeWithExtraOutputsCategory oreProcessingUnitCategory;
    private MachineRecipeWithExtraOutputsCategory grinderCategory;
    private MachineRecipeCategory pressCategory;
    private MachineRecipeWithExtraOutputsCategory extractorCategory;
    private MachineRecipeWithExtraOutputsCategory gemstoneCutterCategory;
    private MachineRecipeWithExtraOutputsCategory enricherCategory;
    private ModularToolRecipeCategory modularToolCategory;

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

        enricherCategory = new MachineRecipeWithExtraOutputsCategory(
            guiHelper,
            new ResourceLocation(MetallurgyPlus.MODID, "enricher"),
            ModBlocks.ENRICHER.get()
        );

        modularToolCategory = new ModularToolRecipeCategory(guiHelper);

        registration.addRecipeCategories(
            alloySmelterCategory,
            oreProcessingUnitCategory,
            grinderCategory,
            pressCategory,
            extractorCategory,
            gemstoneCutterCategory,
            enricherCategory,
            modularToolCategory
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
        List<MachineRecipeWithExtraOutputs> enricherRecipes = recipeManager.getAllRecipesFor(ModRecipeTypes.ENRICHER_TYPE);

        registration.addRecipes(alloySmelterCategory.getRecipeType(), alloySmelterRecipes);
        registration.addRecipes(oreProcessingUnitCategory.getRecipeType(), oreProcessingUnitRecipes);
        registration.addRecipes(grinderCategory.getRecipeType(), grinderRecipes);
        registration.addRecipes(pressCategory.getRecipeType(), pressRecipes);
        registration.addRecipes(extractorCategory.getRecipeType(), extractorRecipes);
        registration.addRecipes(gemstoneCutterCategory.getRecipeType(), gemstoneCutterRecipes);
        registration.addRecipes(enricherCategory.getRecipeType(), enricherRecipes);
        registration.addRecipes(modularToolCategory.getRecipeType(), createModularToolExamples());
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

        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.ENRICHER.get()),
            enricherCategory.getRecipeType()
        );

        registration.addRecipeCatalyst(
            new ItemStack(Items.CRAFTING_TABLE),
            modularToolCategory.getRecipeType()
        );
    }

    private static List<ModularToolJeiRecipe> createModularToolExamples() {
        Item ironHead = ModItems.VANILLA_MAP.get("iron_pickaxe-head").get();
        Item ironRod = ModItems.VANILLA_MAP.get("iron_rod").get();
        ModularPickaxeItem pickaxe = ModItems.MODULAR_PICKAXE.get();
        ItemStack defaultPickaxe = pickaxe.createConfiguredStack(new ItemStack(ironHead), new ItemStack(ironRod));

        List<ModularToolJeiRecipe> recipes = new ArrayList<>();

        List<ItemStack> assemblyInputs = emptyCraftingGrid();
        assemblyInputs.set(0, new ItemStack(ironHead));
        assemblyInputs.set(1, new ItemStack(ironRod));
        recipes.add(new ModularToolJeiRecipe(
            assemblyInputs,
            defaultPickaxe
        ));

        ItemStack damagedPickaxe = defaultPickaxe.copy();
        damagedPickaxe.getOrCreateTag().putInt(
            ModularTool.CUSTOM_DAMAGE_TAG,
            Math.max(1, pickaxe.getMaxDurability(damagedPickaxe) / 2)
        );
        ItemStack repairedPickaxe = damagedPickaxe.copy();
        pickaxe.repair(repairedPickaxe, Math.max(1, pickaxe.getMaxDurability(repairedPickaxe) / 4));
        List<ItemStack> repairInputs = emptyCraftingGrid();
        repairInputs.set(0, damagedPickaxe);
        repairInputs.set(1, new ItemStack(Items.IRON_INGOT));
        recipes.add(new ModularToolJeiRecipe(repairInputs, repairedPickaxe));

        ItemStack speedPickaxe = defaultPickaxe.copy();
        pickaxe.addMiningSpeedBonus(speedPickaxe, 0.5F);
        pickaxe.setModifiers(speedPickaxe, pickaxe.getModifiers(defaultPickaxe) - 1);
        recipes.add(new ModularToolJeiRecipe(
            createModifierInputs(Items.REDSTONE_BLOCK, defaultPickaxe),
            speedPickaxe
        ));

        ItemStack fortunePickaxe = defaultPickaxe.copy();
        pickaxe.addFortuneBonus(fortunePickaxe, 0.5F);
        pickaxe.setModifiers(fortunePickaxe, pickaxe.getModifiers(defaultPickaxe) - 1);
        recipes.add(new ModularToolJeiRecipe(
            createModifierInputs(Items.LAPIS_BLOCK, defaultPickaxe),
            fortunePickaxe
        ));

        ItemStack silkTouchPickaxe = defaultPickaxe.copy();
        pickaxe.addSilkTouch(silkTouchPickaxe);
        pickaxe.setModifiers(silkTouchPickaxe, pickaxe.getModifiers(defaultPickaxe) - 1);
        recipes.add(new ModularToolJeiRecipe(
            createModifierInputs(Items.AMETHYST_BLOCK, defaultPickaxe),
            silkTouchPickaxe
        ));

        return recipes;
    }

    private static List<ItemStack> createModifierInputs(Item material, ItemStack tool) {
        List<ItemStack> inputs = emptyCraftingGrid();
        for (int slot = 0; slot < 9; slot++) {
            if (slot == 4) {
                inputs.set(slot, tool.copy());
            } else {
                inputs.set(slot, new ItemStack(material));
            }
        }
        return inputs;
    }

    private static List<ItemStack> emptyCraftingGrid() {
        return new ArrayList<>(Collections.nCopies(9, ItemStack.EMPTY));
    }
}
