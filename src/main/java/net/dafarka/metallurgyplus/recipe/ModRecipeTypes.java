package net.dafarka.metallurgyplus.recipe;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.recipe.type.MachineRecipeType;
import net.dafarka.metallurgyplus.recipe.type.MachineRecipeWithExtraOutputsType;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
        DeferredRegister.create(
            ForgeRegistries.RECIPE_TYPES,
            MetallurgyPlus.MODID
        );

    public static final RecipeType<MachineRecipeWithExtraOutputs>
        ORE_PROCESSING_UNIT_TYPE =
        new MachineRecipeWithExtraOutputsType();

    public static final RecipeType<MachineRecipe>
        ALLOY_SMELTER_TYPE =
        new MachineRecipeType();

    public static final RecipeType<MachineRecipeWithExtraOutputs>
        GRINDER_TYPE =
        new MachineRecipeWithExtraOutputsType();

    public static final RecipeType<MachineRecipe>
        PRESS_TYPE =
        new MachineRecipeType();

    public static final RecipeType<MachineRecipeWithExtraOutputs>
        EXTRACTOR_TYPE =
        new MachineRecipeWithExtraOutputsType();

    public static final RecipeType<MachineRecipeWithExtraOutputs>
        GEMSTONE_CUTTER_TYPE =
        new MachineRecipeWithExtraOutputsType();


    public static final RegistryObject<RecipeType<MachineRecipeWithExtraOutputs>>
        ORE_PROCESSING_UNIT =
        RECIPE_TYPES.register(
            "ore_processing_unit",
            () -> ORE_PROCESSING_UNIT_TYPE
        );

    public static final RegistryObject<RecipeType<MachineRecipe>>
        ALLOY_SMELTER =
        RECIPE_TYPES.register(
            "alloy_smelter",
            () -> ALLOY_SMELTER_TYPE
        );

    public static final RegistryObject<RecipeType<MachineRecipeWithExtraOutputs>>
        GRINDER =
        RECIPE_TYPES.register(
            "grinder",
            () -> GRINDER_TYPE
        );

    public static final RegistryObject<RecipeType<MachineRecipe>>
        PRESS =
        RECIPE_TYPES.register(
            "press",
            () -> PRESS_TYPE
        );

    public static final RegistryObject<RecipeType<MachineRecipeWithExtraOutputs>>
        EXTRACTOR =
        RECIPE_TYPES.register(
            "extractor",
            () -> EXTRACTOR_TYPE
        );

    public static final RegistryObject<RecipeType<MachineRecipeWithExtraOutputs>>
        GEMSTONE_CUTTER =
        RECIPE_TYPES.register(
            "gemstone_cutter",
            () -> GEMSTONE_CUTTER_TYPE
        );

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
    }
}