package net.dafarka.metallurgyplus.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class AlloySmelterRecipe implements Recipe<SimpleContainer> {
    private final NonNullList<Ingredient> inputItems;
    private final NonNullList<Integer> inputAmounts;
    private final ItemStack output;
    private final ResourceLocation id;

    public AlloySmelterRecipe(NonNullList<Ingredient> inputItems, NonNullList<Integer> inputAmounts, ItemStack output, ResourceLocation id) {
        this.inputItems = inputItems;
        this.inputAmounts = inputAmounts;
        this.output = output;
        this.id = id;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide) {
            return false;
        }

        int inputItemValidityCount = 0;
        for (int i = 0; i < inputItems.size(); i++) {
            for (int j = 0; j < pContainer.getContainerSize(); j++) {
                if (inputItems.get(i).test(pContainer.getItem(j))) {
                    inputItemValidityCount++;
                    break;
                }
            }
        }

        return inputItemValidityCount == inputItems.size();
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputItems;
    }

    public NonNullList<Integer> getInputAmounts() {
        return inputAmounts;
    }

    public int getInputAmountForIngredient(Ingredient ingredient) {
        int index = UtilRecipe.findIngredientIndex(inputItems, ingredient);
        if (index != -1) {
            return inputAmounts.get(index);
        }
        return 1;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<AlloySmelterRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<AlloySmelterRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public AlloySmelterRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);
            NonNullList<Integer> inputAmounts = NonNullList.withSize(ingredients.size(), 1);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
                inputAmounts.set(i, GsonHelper.getAsInt((JsonObject) ingredients.get(i), "count"));
            }

            return new AlloySmelterRecipe(inputs, inputAmounts, output, pRecipeId);
        }

        @Override
        public @Nullable AlloySmelterRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(pBuffer.readInt(), Ingredient.EMPTY);
            NonNullList<Integer> inputAmounts = NonNullList.withSize(pBuffer.readInt(), 1);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(pBuffer));
                inputAmounts.set(i, pBuffer.readInt());
            }

            ItemStack output = pBuffer.readItem();
            return new AlloySmelterRecipe(inputs, inputAmounts, output, pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, AlloySmelterRecipe pRecipe) {
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
