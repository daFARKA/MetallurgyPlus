package net.dafarka.metallurgyplus.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.recipe.ExtractorRecipe;
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

public class ExtractorRecipeProvider extends RecipeProvider {

    public ExtractorRecipeProvider(PackOutput pOutput) {
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

        // From this point onwards custom recipes can be added. Use the below example to construct recipes.
        /*
        inputSize = 1;
        inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
        inputAmounts = NonNullList.withSize(inputSize, 1);
        inputItems.set(0, Utility.getItem("coal"));
        inputAmounts.set(0, 1);
        output = new ItemStack(Utility.getItem("brick"), 1);
        extraOutputs = NonNullList.withSize(1, ItemStack.EMPTY);
        extraChances = NonNullList.withSize(extraOutputs.size(), 1f);
        extraOutputs.set(0, new ItemStack(Utility.getItem("redstone"), 1));
        extraChances.set(0, 0.25f));
        addExtractorFinishedRecipe(pWriter, inputItems, inputAmounts, output, extraOutputs, new ResourceLocation(MetallurgyPlus.MODID, "test_extractor"));*/

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

        List<Item> logTypes = List.of(Items.OAK_LOG, Items.ACACIA_LOG, Items.BIRCH_LOG, Items.CHERRY_LOG, Items.JUNGLE_LOG, Items.DARK_OAK_LOG, Items.MANGROVE_LOG, Items.SPRUCE_LOG, Items.WARPED_STEM, Items.CRIMSON_STEM);
        for (Item logType : logTypes) {
            String currentName = logType.getDescriptionId();
            String currentWoodName = currentName.split("\\.")[2];

            if (!oldMaterials.contains(currentWoodName)) {
                oldMaterials.add(currentWoodName);

                inputSize = 1;
                inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
                inputAmounts = NonNullList.withSize(inputSize, 1);
                inputItems.set(0, Utility.getItem(currentWoodName));
                inputAmounts.set(0, 4);
                output = new ItemStack(ModItems.CUSTOM_ITEM_MAP.get("tree_sap").get(), 1);
                extraOutputs = NonNullList.withSize(1, ItemStack.EMPTY);
                extraChances = NonNullList.withSize(extraOutputs.size(), 1f);
                extraOutputs.set(0, new ItemStack(ModItems.CUSTOM_ITEM_MAP.get("saw_dust").get(), 2));
                extraChances.set(0, 1f);
                addExtractorFinishedRecipe(pWriter, inputItems, inputAmounts, output, extraOutputs, extraChances, new ResourceLocation(MetallurgyPlus.MODID, currentWoodName + "_to_sap_extractor"));
            }
        }
    }

    private void addExtractorFinishedRecipe(Consumer<FinishedRecipe> pWriter, NonNullList<Item> inputItems, NonNullList<Integer> inputAmounts,
                                       ItemStack output, NonNullList<ItemStack> extraOutputs, NonNullList<Float> extraChances, ResourceLocation id) {
        pWriter.accept(new ExtractorFinishedRecipe(inputItems, inputAmounts, output, extraOutputs, extraChances, id));
    }

    private static class ExtractorFinishedRecipe implements FinishedRecipe {
        private final NonNullList<Item> inputItems;
        private final NonNullList<Integer> inputAmounts;
        private final ItemStack output;
        private final NonNullList<ItemStack> extraOutputs;
        private final NonNullList<Float> extraChances;
        private final ResourceLocation id;

        private ExtractorFinishedRecipe(NonNullList<Item> inputItems, NonNullList<Integer> inputAmounts, ItemStack output, NonNullList<ItemStack> extraOutputs, NonNullList<Float> extraChances, ResourceLocation id) {
            this.inputItems = inputItems;
            this.inputAmounts = inputAmounts;
            this.output = output;
            this.extraOutputs = extraOutputs;
            this.extraChances = extraChances;
            this.id = id;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            pJson.addProperty("type","metallurgyplus:extractor");

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

            JsonObject outputObj = new JsonObject();
            outputObj.addProperty("item", Utility.formatResourceName(output.getDescriptionId()));
            outputObj.addProperty("count", output.getCount());
            pJson.add("output", outputObj);

            JsonArray extraOutputArray = new JsonArray();
            i = 0;
            if (extraOutputs != null) {
                for (ItemStack extraOutput : extraOutputs) {
                    JsonObject ingredientObj = new JsonObject();
                    ingredientObj.addProperty("item", Utility.formatResourceName(extraOutput.getItem().getDescriptionId()));
                    ingredientObj.addProperty("count", extraOutput.getCount());
                    ingredientObj.addProperty("chance", extraChances.get(i));
                    extraOutputArray.add(ingredientObj);
                    i++;
                }
                pJson.add("extra_outputs", extraOutputArray);
            }
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ExtractorRecipe.Serializer.INSTANCE;
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
