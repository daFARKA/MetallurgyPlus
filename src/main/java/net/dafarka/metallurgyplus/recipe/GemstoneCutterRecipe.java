package net.dafarka.metallurgyplus.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

public class GemstoneCutterRecipe extends MachineRecipe {

    public GemstoneCutterRecipe(NonNullList<Ingredient> inputItems, NonNullList<Integer> inputAmounts, ItemStack output, ResourceLocation id) {
        super(inputItems, inputAmounts, output, id);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<GemstoneCutterRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<GemstoneCutterRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public GemstoneCutterRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);
            NonNullList<Integer> inputAmounts = NonNullList.withSize(ingredients.size(), 1);

            if (inputs.size() > 1) {
                throw new IllegalStateException("GemstoneCutterRecipe must not have more than 1 input item.");
            }

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
                inputAmounts.set(i, GsonHelper.getAsInt((JsonObject) ingredients.get(i), "count"));
            }

            return new GemstoneCutterRecipe(inputs, inputAmounts, output, pRecipeId);
        }

        @Override
        public @Nullable GemstoneCutterRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(pBuffer.readInt(), Ingredient.EMPTY);
            NonNullList<Integer> inputAmounts = NonNullList.withSize(pBuffer.readInt(), 1);

            if (inputs.size() > 1) {
                throw new IllegalStateException("GemstoneCutterRecipe must not have more than 1 input item.");
            }

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(pBuffer));
                inputAmounts.set(i, pBuffer.readInt());
            }

            ItemStack output = pBuffer.readItem();
            return new GemstoneCutterRecipe(inputs, inputAmounts, output, pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, GemstoneCutterRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.inputItems.size());
            pBuffer.writeInt(pRecipe.inputAmounts.size());

            for (int i = 0; i < pRecipe.inputItems.size(); i++) {
                Ingredient ingredient = pRecipe.getIngredients().get(i);
                ingredient.toNetwork(pBuffer);
                pBuffer.writeInt(pRecipe.getInputAmounts().get(i));
            }

            pBuffer.writeItemStack(pRecipe.getResultItem(null), false);
        }
    }
}
