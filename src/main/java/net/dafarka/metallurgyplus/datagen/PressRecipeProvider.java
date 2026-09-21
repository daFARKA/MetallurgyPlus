package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.datagen.recipe.MachineFinishedRecipe;
import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.recipe.ModRecipeSerializers;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PressRecipeProvider extends RecipeProvider {

    public PressRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        NonNullList<Item> inputItems;
        NonNullList<Integer> inputAmounts;
        ItemStack output;

        // From this point onwards custom recipes can be added. Use the below example to construct recipes.
        /*
        inputItems = NonNullList.withSize(1, new Item(new Item.Properties()));
        inputAmounts = NonNullList.withSize(1, 1);
        inputItems.set(0, Utility.getItem("coal"));
        inputAmounts.set(0, 1);
        output = new ItemStack(Utility.getItem("brick"), 1);
        addPressRecipe(pWriter, inputItems, inputAmounts, output, new ResourceLocation(MetallurgyPlus.MODID, "test_press"));*/

        List<String> oldMaterials = new ArrayList<>();
        for (RegistryObject<Item> item : ModItems.MATERIAL_MAP.values()) {
            String currentName = item.getId().getPath();
            String currentMaterialName = currentName.split("_")[0];
            if (!oldMaterials.contains(currentMaterialName)) {
                oldMaterials.add(currentMaterialName);

                Item ingot = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[0]).get();
                Item dust = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[1]).get();
                Item gear = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[2]).get();
                Item nugget = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[3]).get();
                Item plate = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[4]).get();
                Item rod = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[5]).get();
                Item raw = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[6]).get();

                inputItems = NonNullList.withSize(1, new Item(new Item.Properties()));
                inputAmounts = NonNullList.withSize(1, 1);
                inputItems.set(0, ingot);
                inputAmounts.set(0, 1);
                output = new ItemStack(plate, 1);
                addPressRecipe(pWriter, inputItems, inputAmounts, output, new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_ingot_to_plate_press"));
            }
        }

        for (RegistryObject<Item> item : ModItems.ALLOY_MAP.values()) {
            String currentName = item.getId().getPath();
            String currentMaterialName = currentName.split("_")[0];
            if (!oldMaterials.contains(currentMaterialName)) {
                oldMaterials.add(currentMaterialName);

                Item ingot = ModItems.ALLOY_MAP.get(currentMaterialName + "_" + ModItems.ALLOY_COMPONENT_NAMES[0]).get();
                Item gear = ModItems.ALLOY_MAP.get(currentMaterialName + "_" + ModItems.ALLOY_COMPONENT_NAMES[1]).get();
                Item nugget = ModItems.ALLOY_MAP.get(currentMaterialName + "_" + ModItems.ALLOY_COMPONENT_NAMES[2]).get();
                Item plate = ModItems.ALLOY_MAP.get(currentMaterialName + "_" + ModItems.ALLOY_COMPONENT_NAMES[3]).get();
                Item rod = ModItems.ALLOY_MAP.get(currentMaterialName + "_" + ModItems.ALLOY_COMPONENT_NAMES[4]).get();

                inputItems = NonNullList.withSize(1, new Item(new Item.Properties()));
                inputAmounts = NonNullList.withSize(1, 1);
                inputItems.set(0, ingot);
                inputAmounts.set(0, 1);
                output = new ItemStack(plate, 1);
                addPressRecipe(pWriter, inputItems, inputAmounts, output, new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_ingot_to_plate_press"));
            }
        }

        for (RegistryObject<Item> item : ModItems.VANILLA_MAP.values()) {
            String currentName = item.getId().getPath();
            String currentMaterialName = currentName.split("_")[0];
            if (!oldMaterials.contains(currentMaterialName)) {
                oldMaterials.add(currentMaterialName);

                Item ingot = Utility.getItem(currentMaterialName + "_ingot");
                if (ingot == Items.AIR) {
                    ingot = Utility.getItem(currentMaterialName);
                }

                Item dust = ModItems.VANILLA_MAP.get(currentMaterialName + "_" + ModItems.VANILLA_COMPONENT_NAMES[0]).get();
                Item gear = ModItems.VANILLA_MAP.get(currentMaterialName + "_" + ModItems.VANILLA_COMPONENT_NAMES[1]).get();
                Item plate = ModItems.VANILLA_MAP.get(currentMaterialName + "_" + ModItems.VANILLA_COMPONENT_NAMES[2]).get();
                Item rod = ModItems.VANILLA_MAP.get(currentMaterialName + "_" + ModItems.VANILLA_COMPONENT_NAMES[3]).get();

                if (ingot != Items.AIR) {
                    inputItems = NonNullList.withSize(1, new Item(new Item.Properties()));
                    inputAmounts = NonNullList.withSize(1, 1);
                    inputItems.set(0, ingot);
                    inputAmounts.set(0, 1);
                    output = new ItemStack(plate, 1);
                    addPressRecipe(pWriter, inputItems, inputAmounts, output, new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_ingot_to_plate_press"));
                }
            }
        }
    }

    private void addPressRecipe(Consumer<FinishedRecipe> pWriter, NonNullList<Item> inputItems, NonNullList<Integer> inputAmounts,
                                ItemStack output, ResourceLocation id) {
        pWriter.accept(new MachineFinishedRecipe(
            inputItems, inputAmounts, output, id, "metallurgyplus:press", ModRecipeSerializers.PRESS_SERIALIZER.get()
        ));
    }
}
