package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.datagen.recipe.MachineFinishedRecipe;
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

public class AlloySmelterRecipeProvider extends RecipeProvider {

    public AlloySmelterRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        int inputSize;
        NonNullList<Item> inputItems;
        NonNullList<Integer> inputAmounts;
        ItemStack output;

        // From this point onwards custom recipes can be added. Use the below example to construct recipes.
        /*
        inputSize = 2;
        inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
        inputAmounts = NonNullList.withSize(inputSize, 1);
        inputItems.set(0, Utility.getItem("coal"));
        inputAmounts.set(0, 1);
        inputItems.set(1, Utility.getItem("clay"));
        inputAmounts.set(1, 4);
        output = new ItemStack(utility.getItem("brick"), 1);
        addAlloySmelterRecipe(pWriter, inputItems, inputAmounts, output, new ResourceLocation(MetallurgyPlus.MODID, "test_alloy_smelter"));*/

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


            }
        }
    }

    private void addAlloySmelterRecipe(Consumer<FinishedRecipe> pWriter, NonNullList<Item> inputItems, NonNullList<Integer> inputAmounts,
                                       ItemStack output, ResourceLocation id) {
        pWriter.accept(new MachineFinishedRecipe(
            inputItems, inputAmounts, output, id, "metallurgyplus:alloy_smelter", ModRecipeSerializers.ALLOY_SMELTER_SERIALIZER.get()
        ));
    }
}
