package net.dafarka.metallurgyplus.recipe.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dafarka.metallurgyplus.recipe.MachineRecipeWithExtraOutputs;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;

import javax.annotation.Nullable;

public class MachineRecipeExtraOutputSerializer implements RecipeSerializer<MachineRecipeWithExtraOutputs> {

    private final RecipeType<MachineRecipeWithExtraOutputs> type;

    public MachineRecipeExtraOutputSerializer(RecipeType<MachineRecipeWithExtraOutputs> type) {
        this.type = type;
    }

    @Override
    public MachineRecipeWithExtraOutputs fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
        ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(serializedRecipe, "output"));

        JsonArray ingredients = GsonHelper.getAsJsonArray(serializedRecipe, "ingredients");
        NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);
        NonNullList<Integer> inputAmounts = NonNullList.withSize(ingredients.size(), 1);

        for (int i = 0; i < inputs.size(); i++) {
            inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            inputAmounts.set(i, GsonHelper.getAsInt((JsonObject) ingredients.get(i), "count"));
        }

        NonNullList<ItemStack> extraOutputs = null;
        NonNullList<Double> extraOutputChances = null;
        if (serializedRecipe.has("extra_outputs")) {
            JsonArray extraOutputsJson = GsonHelper.getAsJsonArray(serializedRecipe, "extra_outputs");
            extraOutputs = NonNullList.withSize(extraOutputsJson.size(), ItemStack.EMPTY);
            extraOutputChances = NonNullList.withSize(extraOutputsJson.size(), 1.0);
            for (int i = 0; i < extraOutputs.size(); i++) {
                extraOutputs.set(i, new ItemStack(Utility.getItem(extraOutputsJson.get(i).getAsJsonObject().get("item").getAsString().split(":")[1]),
                    extraOutputsJson.get(i).getAsJsonObject().get("count").getAsInt()));
                if (extraOutputsJson.get(i).getAsJsonObject().has("chance")) {
                    extraOutputChances.set(i, extraOutputsJson.get(i).getAsJsonObject().get("chance").getAsDouble());
                }
            }
        }

        return new MachineRecipeWithExtraOutputs(type, this, inputs, inputAmounts, output, extraOutputs, extraOutputChances, recipeId);
    }

    @Nullable
    @Override
    public MachineRecipeWithExtraOutputs fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        int inputSize = buffer.readInt();
        NonNullList<Ingredient> inputs = NonNullList.withSize(inputSize, Ingredient.EMPTY);
        NonNullList<Integer> inputAmounts = NonNullList.withSize(inputSize, 1);

        for (int i = 0; i < inputs.size(); i++) {
            inputs.set(i, Ingredient.fromNetwork(buffer));
            inputAmounts.set(i, buffer.readInt());
        }

        ItemStack output = buffer.readItem();

        int extraOutputsSize = buffer.readInt();
        NonNullList<ItemStack> extraOutputs = null;
        NonNullList<Double> extraOutputChances = null;
        if (extraOutputsSize > 0) {
            extraOutputs = NonNullList.withSize(extraOutputsSize, ItemStack.EMPTY);
            extraOutputChances = NonNullList.withSize(extraOutputsSize, 1.0);
            for (int i = 0; i < extraOutputsSize; i++) {
                extraOutputs.set(i, buffer.readItem());
                extraOutputChances.set(i, buffer.readDouble());
            }
        }

        return new MachineRecipeWithExtraOutputs(type, this, inputs, inputAmounts, output, extraOutputs, extraOutputChances, recipeId);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, MachineRecipeWithExtraOutputs recipe) {
        buffer.writeInt(recipe.inputItems.size());

        for (int i = 0; i < recipe.inputItems.size(); i++) {
            Ingredient ingredient = recipe.getIngredients().get(i);
            ingredient.toNetwork(buffer);
            buffer.writeInt(recipe.getInputAmounts().get(i));
        }

        buffer.writeItemStack(recipe.getResultItem(null), false);

        NonNullList<ItemStack> extraOutputs = recipe.getExtraOutputs();
        NonNullList<Double> extraOutputChances = recipe.getExtraOutputChances();
        if (extraOutputs != null) {
            buffer.writeInt(extraOutputs.size());
            int i = 0;
            for (ItemStack extraResult : recipe.getExtraOutputs()) {
                buffer.writeItemStack(extraResult, false);
                buffer.writeDouble(extraOutputChances.get(i));
                i++;
            }
        } else {
            buffer.writeInt(0);
        }
    }
}