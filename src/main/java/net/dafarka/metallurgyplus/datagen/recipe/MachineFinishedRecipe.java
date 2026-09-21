package net.dafarka.metallurgyplus.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nullable;

public class MachineFinishedRecipe implements FinishedRecipe {
    private final NonNullList<Item> inputItems;
    private final NonNullList<Integer> inputAmounts;
    private final ItemStack output;
    private final ResourceLocation id;
    private final String recipeType;
    private final RecipeSerializer<?> serializer;

    public MachineFinishedRecipe(
        NonNullList<Item> inputItems,
        NonNullList<Integer> inputAmounts,
        ItemStack output,
        ResourceLocation id,
        String recipeType,
        RecipeSerializer<?> serializer
    ) {
        this.inputItems = inputItems;
        this.inputAmounts = inputAmounts;
        this.output = output;
        this.id = id;
        this.recipeType = recipeType;
        this.serializer = serializer;
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
        json.addProperty("type", recipeType);

        JsonArray ingredientsArray = new JsonArray();

        for (int i = 0; i < inputItems.size(); i++) {
            JsonObject ingredientObj = new JsonObject();

            ingredientObj.addProperty("item", Utility.formatResourceName(inputItems.get(i).getDescriptionId()));

            ingredientObj.addProperty("count", inputAmounts.get(i));

            ingredientsArray.add(ingredientObj);
        }

        json.add("ingredients", ingredientsArray);

        JsonObject outputObj = new JsonObject();

        outputObj.addProperty("item", Utility.formatResourceName(output.getDescriptionId()));

        outputObj.addProperty("count", output.getCount());

        json.add("output", outputObj);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return serializer;
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