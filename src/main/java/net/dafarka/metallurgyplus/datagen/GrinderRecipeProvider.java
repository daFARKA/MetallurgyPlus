package net.dafarka.metallurgyplus.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.recipe.OreProcessingUnitRecipe;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GrinderRecipeProvider extends RecipeProvider {
    private static Utility utility = new Utility();

    public GrinderRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        int inputSize;
        NonNullList<Item> inputItems;
        NonNullList<Integer> inputAmounts;
        ItemStack output;
        NonNullList<ItemStack> extraOutputs = null;

        // From this point onwards custom recipes can be added. Use the below example to construct recipes.
        /*
        inputSize = 1;
        inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
        inputAmounts = NonNullList.withSize(inputSize, 1);
        inputItems.set(0, utility.getItem("coal"));
        inputAmounts.set(0, 1);
        output = new ItemStack(utility.getItem("brick"), 1);
        extraOutputs = NonNullList.withSize(1, ItemStack.EMPTY);
        extraOutputs.set(0, new ItemStack(utility.getItem("redstone"), 1));
        addGrinderFinishedRecipe(pWriter, inputItems, inputAmounts, output, extraOutputs, new ResourceLocation(MetallurgyPlus.MODID, "test_grinder"));*/

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

                inputSize = 1;
                inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
                inputAmounts = NonNullList.withSize(inputSize, 1);
                inputItems.set(0, ingot);
                inputAmounts.set(0, 1);
                output = new ItemStack(dust, 1);
                addGrinderFinishedRecipe(pWriter, inputItems, inputAmounts, output, extraOutputs, new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_ingot_to_dust_grinder"));

                inputSize = 1;
                inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
                inputAmounts = NonNullList.withSize(inputSize, 1);
                inputItems.set(0, raw);
                inputAmounts.set(0, 1);
                output = new ItemStack(dust, 2);
                addGrinderFinishedRecipe(pWriter, inputItems, inputAmounts, output, extraOutputs, new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_raw_to_dust_grinder"));
            }
        }

        for (RegistryObject<Item> item : ModItems.ORE_MAP.values()) {
            String currentName = item.getId().getPath();
            String currentMaterialName = currentName.split("_")[0];
            if (!oldMaterials.contains(currentMaterialName)) {
                oldMaterials.add(currentMaterialName);

                Item raw = ModItems.ORE_MAP.get(currentMaterialName + "_" + ModItems.ORE_COMPONENT_NAMES[0]).get();
                Item dust = ModItems.ORE_MAP.get(currentMaterialName + "_" + ModItems.ORE_COMPONENT_NAMES[1]).get();

                Item ore_stone = ModBlocks.ORE_BLOCKS_MAP.get(currentMaterialName + "_" + ModItems.ORE_BASE_NAME[0] + "_block").get().asItem();
                Item ore_deepslate = ModBlocks.ORE_BLOCKS_MAP.get(currentMaterialName + "_" + ModItems.ORE_BASE_NAME[1] + "_block").get().asItem();

                inputSize = 1;
                inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
                inputAmounts = NonNullList.withSize(inputSize, 1);
                inputItems.set(0, ore_stone.asItem());
                inputAmounts.set(0, 1);
                output = new ItemStack(raw, 3);
                addGrinderFinishedRecipe(pWriter, inputItems, inputAmounts, output, extraOutputs, new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_ore_stone_to_raw_grinder"));

                inputSize = 1;
                inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
                inputAmounts = NonNullList.withSize(inputSize, 1);
                inputItems.set(0, ore_deepslate.asItem());
                inputAmounts.set(0, 1);
                output = new ItemStack(raw, 3);
                addGrinderFinishedRecipe(pWriter, inputItems, inputAmounts, output, extraOutputs, new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_ore_deepslate_to_raw_grinder"));
            }
        }
    }

    private void addGrinderFinishedRecipe(Consumer<FinishedRecipe> pWriter, NonNullList<Item> inputItems, NonNullList<Integer> inputAmounts,
                                       ItemStack output, NonNullList<ItemStack> extraOutputs, ResourceLocation id) {
        pWriter.accept(new GrinderFinishedRecipe(inputItems, inputAmounts, output, extraOutputs, id));
    }

    private static class GrinderFinishedRecipe implements FinishedRecipe {
        private final NonNullList<Item> inputItems;
        private final NonNullList<Integer> inputAmounts;
        private final ItemStack output;
        private final NonNullList<ItemStack> extraOutputs;
        private final ResourceLocation id;

        private GrinderFinishedRecipe(NonNullList<Item> inputItems, NonNullList<Integer> inputAmounts, ItemStack output, NonNullList<ItemStack> extraOutputs, ResourceLocation id) {
            this.inputItems = inputItems;
            this.inputAmounts = inputAmounts;
            this.output = output;
            this.extraOutputs = extraOutputs;
            this.id = id;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            pJson.addProperty("type","metallurgyplus:grinder");

            JsonArray ingredientsArray = new JsonArray();
            int i = 0;
            for (Item ingredient : inputItems) {
                JsonObject ingredientObj = new JsonObject();
                ingredientObj.addProperty("item", utility.formatResourceName(ingredient.getDescriptionId()));
                ingredientObj.addProperty("count", inputAmounts.get(i));
                ingredientsArray.add(ingredientObj);
                i++;
            }
            pJson.add("ingredients", ingredientsArray);

            JsonObject outputObj = new JsonObject();
            outputObj.addProperty("item", utility.formatResourceName(output.getDescriptionId()));
            outputObj.addProperty("count", output.getCount());
            pJson.add("output", outputObj);

            JsonArray extraOutputArray = new JsonArray();
            if (extraOutputs != null) {
                for (ItemStack extraOutput : extraOutputs) {
                    JsonObject ingredientObj = new JsonObject();
                    ingredientObj.addProperty("item", utility.formatResourceName(extraOutput.getItem().getDescriptionId()));
                    ingredientObj.addProperty("count", extraOutput.getCount());
                    extraOutputArray.add(ingredientObj);
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
            return OreProcessingUnitRecipe.Serializer.INSTANCE;
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
