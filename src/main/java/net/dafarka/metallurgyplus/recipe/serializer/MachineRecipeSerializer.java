package net.dafarka.metallurgyplus.recipe.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dafarka.metallurgyplus.recipe.MachineRecipe;
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

public class MachineRecipeSerializer implements RecipeSerializer<MachineRecipe> {

    private final RecipeType<MachineRecipe> type;

    public MachineRecipeSerializer(RecipeType<MachineRecipe> type) {
        this.type = type;
    }

    @Override
    public MachineRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

        JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");

        NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);

        NonNullList<Integer> inputAmounts = NonNullList.withSize(ingredients.size(), 1);

        for (int i = 0; i < inputs.size(); i++) {
            inputs.set(i, Ingredient.fromJson(ingredients.get(i)));

            inputAmounts.set(i, GsonHelper.getAsInt(ingredients.get(i).getAsJsonObject(), "count"));
        }

        return new MachineRecipe(type, this, inputs, inputAmounts, output, recipeId);
    }

    @Nullable
    @Override
    public MachineRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        int inputSize = buffer.readInt();

        NonNullList<Ingredient> inputs = NonNullList.withSize(inputSize, Ingredient.EMPTY);

        NonNullList<Integer> inputAmounts = NonNullList.withSize(inputSize, 1);

        for (int i = 0; i < inputSize; i++) {
            inputs.set(i, Ingredient.fromNetwork(buffer));

            inputAmounts.set(i, buffer.readInt());
        }

        ItemStack output = buffer.readItem();

        return new MachineRecipe(type, this, inputs, inputAmounts, output, recipeId);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, MachineRecipe recipe) {
        buffer.writeInt(recipe.inputItems.size());

        for (int i = 0; i < recipe.inputItems.size(); i++) {
            recipe.inputItems.get(i).toNetwork(buffer);
            buffer.writeInt(
                recipe.inputAmounts.get(i)
            );
        }

        buffer.writeItemStack(recipe.output, false);
    }
}