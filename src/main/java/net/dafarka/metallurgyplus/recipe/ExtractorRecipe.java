package net.dafarka.metallurgyplus.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.util.Utility;
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

public class ExtractorRecipe implements Recipe<SimpleContainer> {
    private final NonNullList<Ingredient> inputItems;
    private final NonNullList<Integer> inputAmounts;
    private final ItemStack output;
    private final NonNullList<ItemStack> extraOutputs;
    private final NonNullList<Double> extraOutputChances;
    private final ResourceLocation id;

    public ExtractorRecipe(NonNullList<Ingredient> inputItems, NonNullList<Integer> inputAmounts, ItemStack output, NonNullList<ItemStack> extraOutputs,
                           NonNullList<Double> extraOutputChances, ResourceLocation id) {
        this.inputItems = inputItems;
        this.inputAmounts = inputAmounts;
        this.output = output;
        this.extraOutputs = extraOutputs;
        this.extraOutputChances = extraOutputChances;
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

    public NonNullList<ItemStack> getExtraOutputs() {
        return extraOutputs;
    }

    public NonNullList<Double> getExtraOutputChances() {
        return extraOutputChances;
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

    public static class Type implements RecipeType<ExtractorRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements  RecipeSerializer<ExtractorRecipe> {
        public static final ExtractorRecipe.Serializer INSTANCE = new ExtractorRecipe.Serializer();
        public static final ResourceLocation ID = new ResourceLocation(MetallurgyPlus.MODID, "extractor");

        @Override
        public ExtractorRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);
            NonNullList<Integer> inputAmounts = NonNullList.withSize(ingredients.size(), 1);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
                inputAmounts.set(i, GsonHelper.getAsInt((JsonObject) ingredients.get(i), "count"));
            }

            NonNullList<ItemStack> extraOutputs = null;
            NonNullList<Double> extraOutputChances = null;
            if (pSerializedRecipe.has("extra_outputs")) {
                JsonArray extraOutputsJson = GsonHelper.getAsJsonArray(pSerializedRecipe, "extra_outputs");
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

            return new ExtractorRecipe(inputs, inputAmounts, output, extraOutputs, extraOutputChances, pRecipeId);
        }

        @Override
        public @Nullable ExtractorRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            int inputSize = pBuffer.readInt();
            NonNullList<Ingredient> inputs = NonNullList.withSize(inputSize, Ingredient.EMPTY);
            NonNullList<Integer> inputAmounts = NonNullList.withSize(inputSize, 1);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(pBuffer));
                inputAmounts.set(i, pBuffer.readInt());
            }

            ItemStack output = pBuffer.readItem();

            int extraOutputsSize = pBuffer.readInt();
            NonNullList<ItemStack> extraOutputs = null;
            NonNullList<Double> extraOutputChances = null;
            if (extraOutputsSize > 0) {
                extraOutputs = NonNullList.withSize(extraOutputsSize, ItemStack.EMPTY);
                extraOutputChances = NonNullList.withSize(extraOutputsSize, 1.0);
                for (int i = 0; i < extraOutputsSize; i++) {
                    extraOutputs.set(i, pBuffer.readItem());
                    extraOutputChances.set(i, pBuffer.readDouble());
                }
            }

            return new ExtractorRecipe(inputs, inputAmounts, output, extraOutputs, extraOutputChances, pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, ExtractorRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.inputItems.size());

            for (int i = 0; i < pRecipe.inputItems.size(); i++) {
                Ingredient ingredient = pRecipe.getIngredients().get(i);
                ingredient.toNetwork(pBuffer);
                pBuffer.writeInt(pRecipe.getInputAmounts().get(i));
            }

            pBuffer.writeItemStack(pRecipe.getResultItem(null), false);

            NonNullList<ItemStack> extraOutputs = pRecipe.getExtraOutputs();
            NonNullList<Double> extraOutputChances = pRecipe.getExtraOutputChances();
            if (extraOutputs != null) {
                pBuffer.writeInt(extraOutputs.size());
                int i = 0;
                for (ItemStack extraResult : pRecipe.getExtraOutputs()) {
                    pBuffer.writeItemStack(extraResult, false);
                    pBuffer.writeDouble(extraOutputChances.get(i));
                    i++;
                }
            } else {
                pBuffer.writeInt(0);
            }
        }
    }
}
