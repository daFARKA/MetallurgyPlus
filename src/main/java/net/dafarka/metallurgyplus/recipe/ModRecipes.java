package net.dafarka.metallurgyplus.recipe;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MetallurgyPlus.MODID);

    public static final RegistryObject<RecipeSerializer<AlloySmelterRecipe>> ALLOY_SMELTER_SERIALIZER =
        SERIALIZERS.register("alloy_smelter", () -> AlloySmelterRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<OreProcessingUnitRecipe>> ORE_PROCESSING_UNIT_SERIALIZER =
        SERIALIZERS.register("ore_processing_unit", () -> OreProcessingUnitRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<GrinderRecipe>> GRINDER_SERIALIZER =
        SERIALIZERS.register("grinder", () -> GrinderRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<PressRecipe>> PRESS_SERIALIZER =
        SERIALIZERS.register("press", () -> PressRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
