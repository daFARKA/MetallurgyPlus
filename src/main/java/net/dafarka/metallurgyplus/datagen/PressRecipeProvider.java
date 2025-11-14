package net.dafarka.metallurgyplus.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.recipe.AlloySmelterRecipe;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

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
        pWriter.accept(new PressFinishedRecipe(inputItems, inputAmounts, output, id));
    }

    private static class PressFinishedRecipe implements FinishedRecipe {
        private final NonNullList<Item> inputItems;
        private final NonNullList<Integer> inputAmounts;
        private final ItemStack output;
        private final ResourceLocation id;

        private PressFinishedRecipe(NonNullList<Item> inputItems, NonNullList<Integer> inputAmounts, ItemStack output, ResourceLocation id) {
            this.inputItems = inputItems;
            this.inputAmounts = inputAmounts;
            this.output = output;
            this.id = id;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            pJson.addProperty("type","metallurgyplus:press");

            // Serialize ingredients array
            JsonArray ingredientsArray = new JsonArray();
            int i = 0;
            for (Item ingredient : inputItems) {
                JsonObject ingredientObj = new JsonObject();
                ingredientObj.addProperty("item", Utility.formatResourceName(ingredient.getDescriptionId()));
                ingredientObj.addProperty("count", inputAmounts.get(i));
                ingredientsArray.add(ingredientObj);
                i++;
            }
            pJson.add("ingredients", ingredientsArray);

            // Serialize output
            JsonObject outputObj = new JsonObject();
            outputObj.addProperty("item", Utility.formatResourceName(output.getDescriptionId()));
            outputObj.addProperty("count", output.getCount());
            pJson.add("output", outputObj);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return AlloySmelterRecipe.Serializer.INSTANCE;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
