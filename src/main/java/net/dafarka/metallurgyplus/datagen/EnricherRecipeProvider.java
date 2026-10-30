package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.datagen.recipe.MachineFinishedRecipeWithExtraOutputs;
import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.recipe.ModRecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EnricherRecipeProvider extends RecipeProvider {

    public EnricherRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        int inputSize;
        NonNullList<Item> inputItems;
        NonNullList<Integer> inputAmounts;
        ItemStack output;
        NonNullList<ItemStack> extraOutputs = null;
        NonNullList<Float> extraChances = null;

        // From this point onwards custom recipes can be added. Use the below example to construct recipes. NOTE: inputSize should always be 1 here.
        /*
        inputSize = 1;
        inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
        inputAmounts = NonNullList.withSize(inputSize, 1);
        inputItems.set(0, utility.getItem("coal"));
        inputAmounts.set(0, 1);
        output = new ItemStack(utility.getItem("brick"), 1);
        extraOutputs = NonNullList.withSize(1, ItemStack.EMPTY);
        extraChances = NonNullList.withSize(extraOutputs.size(), 1f);
        extraOutputs.set(0, new ItemStack(utility.getItem("redstone"), 1));
        extraChances.set(0, 0.25f);
        addEnricherRecipe(pWriter, inputItems, inputAmounts, output, extraOutputs, extraChances, new ResourceLocation(MetallurgyPlus.MODID, "test_ore_processing_unit"));*/

        inputSize = 1;
        inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
        inputAmounts = NonNullList.withSize(inputSize, 1);
        inputItems.set(0, ModItems.MATERIAL_MAP.get("uranium_ingot").get());
        inputAmounts.set(0, 1);
        output = new ItemStack(ModItems.FISSION_MAP.get("uranium-238_chunk").get());
        addEnricherRecipe(pWriter, inputItems, inputAmounts, output, null, null, new ResourceLocation(MetallurgyPlus.MODID, "uranium_ingot_enricher"));

        List<String> oldMaterials = new ArrayList<>();
        for (RegistryObject<Item> item : ModItems.FISSION_MAP.values()) {
            String currentName = item.getId().getPath();
            String currentMaterialName = currentName.split("_")[0];
            if (!oldMaterials.contains(currentMaterialName)) {
                oldMaterials.add(currentMaterialName);

                int tier = ModItems.FISSION_TIER_LIST.indexOf(currentMaterialName);
                Item pellet = ModItems.FISSION_MAP.get(currentMaterialName + "_" + ModItems.FISSION_COMPONENT_NAMES[0]).get();
                Item chunk = ModItems.FISSION_MAP.get(currentMaterialName + "_" + ModItems.FISSION_COMPONENT_NAMES[1]).get();
                Item fuel = ModItems.FISSION_MAP.get(currentMaterialName + "_" + ModItems.FISSION_COMPONENT_NAMES[2]).get();
                Item fuel_depleted = ModItems.FISSION_MAP.get(currentMaterialName + "_" + ModItems.FISSION_COMPONENT_NAMES[2] + "-depleted").get();

                inputSize = 1;
                inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
                inputAmounts = NonNullList.withSize(inputSize, 1);
                inputItems.set(0, pellet);
                inputAmounts.set(0, 3);
                output = new ItemStack(fuel);
                addEnricherRecipe(pWriter, inputItems, inputAmounts, output, null, null, new ResourceLocation(currentMaterialName + "_pellet_to_fuel_enricher"));

                if (tier > 0) {
                    String prevTierName = ModItems.FISSION_TIER_LIST.get(tier - 1);
                    Item previous_pellet = ModItems.FISSION_MAP.get(prevTierName + "_" + ModItems.FISSION_COMPONENT_NAMES[0]).get();
                    Item previous_chunk = ModItems.FISSION_MAP.get(prevTierName + "_" + ModItems.FISSION_COMPONENT_NAMES[1]).get();
                    Item previous_fuel = ModItems.FISSION_MAP.get(prevTierName + "_" + ModItems.FISSION_COMPONENT_NAMES[2]).get();
                    Item previous_fuel_depleted = ModItems.FISSION_MAP.get(prevTierName + "_" + ModItems.FISSION_COMPONENT_NAMES[2] + "-depleted").get();

                    inputSize = 1;
                    inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
                    inputAmounts = NonNullList.withSize(inputSize, 1);
                    inputItems.set(0, previous_fuel_depleted);
                    inputAmounts.set(0, 1);
                    output = new ItemStack(previous_pellet, 1);
                    extraOutputs = NonNullList.withSize(5, ItemStack.EMPTY);
                    extraChances = NonNullList.withSize(extraOutputs.size(), 1f);
                    extraOutputs.set(0, new ItemStack(previous_pellet, 1));
                    extraChances.set(0, 0.05f);
                    extraOutputs.set(1, new ItemStack(previous_chunk, 3));
                    extraChances.set(1, 0.25f);
                    extraOutputs.set(2, new ItemStack(chunk, 3));
                    extraChances.set(2, 1f);
                    extraOutputs.set(3, new ItemStack(chunk, 2));
                    extraChances.set(3, 0.50f);
                    extraOutputs.set(4, new ItemStack(chunk, 1));
                    extraChances.set(4, 0.25f);
                    addEnricherRecipe(pWriter, inputItems, inputAmounts, output, extraOutputs, extraChances, new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_prev_depleted_fuel_to_pellets_enricher"));
                }
            }
        }
    }

    private void addEnricherRecipe(Consumer<FinishedRecipe> pWriter, NonNullList<Item> inputItems, NonNullList<Integer> inputAmounts,
                                   ItemStack output, NonNullList<ItemStack> extraOutputs, NonNullList<Float> extraChances, ResourceLocation id) {
        pWriter.accept(new MachineFinishedRecipeWithExtraOutputs(
            inputItems, inputAmounts, output, extraOutputs, extraChances, id, "metallurgyplus:enricher", ModRecipeSerializers.ENRICHER_SERIALIZER.get()
        ));
    }
}
