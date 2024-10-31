package net.dafarka.metallurgyplus.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.entity.UtilBlockEntity;
import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.recipe.AlloySmelterRecipe;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    private AlloySmelterRecipeProvider alloySmelterRecipeProvider;

    private static Utility utility = new Utility();

    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
        alloySmelterRecipeProvider = new AlloySmelterRecipeProvider(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        buildMaterialRecipe(pWriter);

        oreSmelting(pWriter, CLAY_SMELTABLES, RecipeCategory.MISC, Items.CLAY_BALL, 0.25f, 100, "clay");
        oreSmelting(pWriter, AL_SMELTABLES, RecipeCategory.MISC, ModItems.MATERIAL_MAP.get("aluminum_ingot").get(), 0.25f, 100, "clay");

        alloySmelterRecipeProvider.buildRecipes(pWriter);
    }

    private static final List<ItemLike> CLAY_SMELTABLES = List.of(ModItems.CLAY_MINERAL_RAW.get());
    private static final List<ItemLike> AL_SMELTABLES = List.of(ModItems.BAUXITE.get());

    private void buildMaterialRecipe(Consumer<FinishedRecipe> pWriter) {
        List<String> oldMaterials = new ArrayList<>();
        for (RegistryObject<Item> item : ModItems.MATERIAL_MAP.values()){
            String currentName = item.getId().getPath();
            String currentMaterialName = currentName.split("_")[0];
            if (!oldMaterials.contains(currentMaterialName)) {
                oldMaterials.add(currentMaterialName);

                Item ingot = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[0]).get();
                Item dust = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[1]).get();
                Item gear = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[2]).get();
                Item nugget = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[3]).get();
                Item plate = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[4]).get();
                Item rod = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[5]).get();
                Item raw = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[6]).get();

                Block block = ModBlocks.MATERIAL_BLOCKS_MAP.get(currentMaterialName + "_block").get();

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, block)
                    .pattern("XXX")
                    .pattern("XXX")
                    .pattern("XXX")
                    .define('X', ingot)
                    .unlockedBy(getHasName(ingot), has(ingot))
                    .save(pWriter);

                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ingot, 9)
                    .requires(block)
                    .unlockedBy(getHasName(block), has(block))
                    .save(pWriter, new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_ingots_from_block"));

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ingot)
                    .pattern("XXX")
                    .pattern("XXX")
                    .pattern("XXX")
                    .define('X', nugget)
                    .unlockedBy(getHasName(nugget), has(nugget))
                    .save(pWriter, new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_ingot_from_nuggets"));

                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nugget, 9)
                    .requires(ingot)
                    .unlockedBy(getHasName(ingot), has(ingot))
                    .save(pWriter);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, gear)
                    .pattern(" X ")
                    .pattern("X X")
                    .pattern(" X ")
                    .define('X', ingot)
                    .unlockedBy(getHasName(ingot), has(ingot))
                    .save(pWriter);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, rod)
                    .pattern("   ")
                    .pattern("  X")
                    .pattern(" X ")
                    .define('X', ingot)
                    .unlockedBy(getHasName(ingot), has(ingot))
                    .save(pWriter);

                oreSmelting(pWriter, List.of(raw), RecipeCategory.MISC, ingot, 0.25f, 100, currentMaterialName);
            }
        }
    }

    public static class AlloySmelterRecipeProvider extends RecipeProvider {

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
            inputItems.set(0, utility.getItem("coal"));
            inputAmounts.set(0, 1);
            inputItems.set(1, utility.getItem("clay"));
            inputAmounts.set(1, 4);
            output = new ItemStack(utility.getItem("brick"), 1);
            addAlloySmelterRecipe(pWriter, inputItems, inputAmounts, output, new ResourceLocation(MetallurgyPlus.MODID, "test_alloy_smelter"));*/

            List<String> oldMaterials = new ArrayList<>();
            for (RegistryObject<Item> item : ModItems.MATERIAL_MAP.values()) {
                String currentName = item.getId().getPath();
                String currentMaterialName = currentName.split("_")[0];
                if (!oldMaterials.contains(currentMaterialName)) {
                    oldMaterials.add(currentMaterialName);

                    Item ingot = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[0]).get();
                    Item dust = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[1]).get();
                    Item gear = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[2]).get();
                    Item nugget = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[3]).get();
                    Item plate = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[4]).get();
                    Item rod = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[5]).get();
                    Item raw = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.COMPONENT_NAMES[6]).get();

                    inputSize = 2;
                    inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
                    inputAmounts = NonNullList.withSize(inputSize, 1);
                    inputItems.set(0, dust);
                    inputAmounts.set(0, 1);
                    inputItems.set(1, utility.getItem("coal"));
                    inputAmounts.set(1, 1);
                    output = new ItemStack(ingot, 1);
                    addAlloySmelterRecipe(pWriter, inputItems, inputAmounts, output,
                        new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_dust_to_ingot_alloy_smelter"));
                }
            }
        }

        private void addAlloySmelterRecipe(Consumer<FinishedRecipe> pWriter, NonNullList<Item> inputItems, NonNullList<Integer> inputAmounts,
                                           ItemStack output, ResourceLocation id) {
            pWriter.accept(new AlloySmelterFinishedRecipe(inputItems, inputAmounts, output, id));
        }

        private static class AlloySmelterFinishedRecipe implements FinishedRecipe {
            private final NonNullList<Item> inputItems;
            private final NonNullList<Integer> inputAmounts;
            private final ItemStack output;
            private final ResourceLocation id;

            private AlloySmelterFinishedRecipe(NonNullList<Item> inputItems, NonNullList<Integer> inputAmounts, ItemStack output, ResourceLocation id) {
                this.inputItems = inputItems;
                this.inputAmounts = inputAmounts;
                this.output = output;
                this.id = id;
            }

            @Override
            public void serializeRecipeData(JsonObject pJson) {
                pJson.addProperty("type","metallurgyplus:alloy_smelter");

                // Serialize ingredients array
                JsonArray ingredientsArray = new JsonArray();
                int i = 0;
                for (Item ingredient : inputItems) {
                    JsonObject ingredientObj = new JsonObject();
                    ingredientObj.addProperty("item", utility.formatName(ingredient.getDescriptionId()));
                    ingredientObj.addProperty("count", inputAmounts.get(i));
                    ingredientsArray.add(ingredientObj);
                    i++;
                }
                pJson.add("ingredients", ingredientsArray);

                // Serialize output
                JsonObject outputObj = new JsonObject();
                outputObj.addProperty("item", utility.formatName(output.getDescriptionId()));
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
}
