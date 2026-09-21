package net.dafarka.metallurgyplus.recipe;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.recipe.serializer.MachineRecipeExtraOutputSerializer;
import net.dafarka.metallurgyplus.recipe.serializer.MachineRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
        DeferredRegister.create(
            ForgeRegistries.RECIPE_SERIALIZERS,
            MetallurgyPlus.MODID
        );

    public static final RegistryObject<RecipeSerializer<MachineRecipeWithExtraOutputs>> ORE_PROCESSING_UNIT_SERIALIZER =
        SERIALIZERS.register(
            "ore_processing_unit",
            () -> new MachineRecipeExtraOutputSerializer(ModRecipeTypes.ORE_PROCESSING_UNIT_TYPE)
        );

    public static final RegistryObject<RecipeSerializer<MachineRecipe>> ALLOY_SMELTER_SERIALIZER =
        SERIALIZERS.register(
            "alloy_smelter",
            () -> new MachineRecipeSerializer(ModRecipeTypes.ALLOY_SMELTER_TYPE)
        );

    public static final RegistryObject<RecipeSerializer<MachineRecipeWithExtraOutputs>> GRINDER_SERIALIZER =
        SERIALIZERS.register(
            "grinder",
            () -> new MachineRecipeExtraOutputSerializer(ModRecipeTypes.GRINDER_TYPE)
        );

    public static final RegistryObject<RecipeSerializer<MachineRecipe>> PRESS_SERIALIZER =
        SERIALIZERS.register(
            "press",
            () -> new MachineRecipeSerializer(ModRecipeTypes.PRESS_TYPE)
        );


    public static final RegistryObject<RecipeSerializer<MachineRecipeWithExtraOutputs>> EXTRACTOR_SERIALIZER =
        SERIALIZERS.register(
            "extractor",
            () -> new MachineRecipeExtraOutputSerializer(ModRecipeTypes.EXTRACTOR_TYPE)
        );


    public static final RegistryObject<RecipeSerializer<MachineRecipeWithExtraOutputs>> GEMSTONE_CUTTER_SERIALIZER =
        SERIALIZERS.register(
            "gemstone_cutter",
            () -> new MachineRecipeExtraOutputSerializer(ModRecipeTypes.GEMSTONE_CUTTER_TYPE)
        );

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}