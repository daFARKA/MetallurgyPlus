package net.dafarka.metallurgyplus.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dafarka.metallurgyplus.MetallurgyPlus;
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

public class OreProcessingUnitRecipe implements Recipe<SimpleContainer> {
    private final NonNullList<Ingredient> inputItems;
    private final NonNullList<Integer> inputAmounts;
    private final ItemStack output;
    private final NonNullList<ItemStack> extraOutputs;
    private final ResourceLocation id;

    public OreProcessingUnitRecipe(NonNullList<Ingredient> inputItems, NonNullList<Integer> inputAmounts, ItemStack output, NonNullList<ItemStack> extraOutputs,
                                   ResourceLocation id) {
        this.inputItems = inputItems;
        this.inputAmounts = inputAmounts;
        this.output = output;
        this.extraOutputs = extraOutputs;
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

    public NonNullList<ItemStack> getExtraOutputs() {
        return extraOutputs;
    }

    public NonNullList<Ingredient> getIngredientsCustom() {
        return inputItems;
    }

    public NonNullList<Integer> getInputAmounts() {
        return inputAmounts;
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

    public static class Type implements RecipeType<OreProcessingUnitRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "ore_processing_unit";
    }

    public static class Serializer implements  RecipeSerializer<OreProcessingUnitRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(MetallurgyPlus.MODID, "ore_processing_unit");

        @Override
        public OreProcessingUnitRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);
            NonNullList<Integer> inputAmounts = NonNullList.withSize(ingredients.size(), 1);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
                inputAmounts.set(i, GsonHelper.getAsInt((JsonObject) ingredients.get(i), "count"));
            }

            NonNullList<ItemStack> extraOutputs = null;
            if (pSerializedRecipe.has("extra_outputs")) {
                JsonArray extraOutputsJson = GsonHelper.getAsJsonArray(pSerializedRecipe, "extra_outputs");
                extraOutputs = NonNullList.withSize(extraOutputsJson.size(), ItemStack.EMPTY);
                for (int i = 0; i < extraOutputs.size(); i++) {
                    extraOutputs.set(i, ShapedRecipe.itemStackFromJson((JsonObject) extraOutputsJson.get(i)));
                }
            }

            return new OreProcessingUnitRecipe(inputs, inputAmounts, output, extraOutputs, pRecipeId);
        }

        @Override
        public @Nullable OreProcessingUnitRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            int inputSize = pBuffer.readInt();
            NonNullList<Ingredient> inputs = NonNullList.withSize(inputSize, Ingredient.EMPTY);
            NonNullList<Integer> inputAmounts = NonNullList.withSize(inputSize, 1);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(pBuffer));
                inputAmounts.set(i, pBuffer.readInt());
            }

            ItemStack output = pBuffer.readItem();

            NonNullList<ItemStack> extraOutputs = NonNullList.withSize(pBuffer.readInt(), ItemStack.EMPTY);
            for (int i = 0; i < extraOutputs.size(); i++) {
                extraOutputs.set(i, pBuffer.readItem());
            }

            return new OreProcessingUnitRecipe(inputs, inputAmounts, output, extraOutputs, pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, OreProcessingUnitRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.inputItems.size());

            for (int i = 0; i < pRecipe.inputItems.size(); i++) {
                Ingredient ingredient = pRecipe.getIngredientsCustom().get(i);
                ingredient.toNetwork(pBuffer);
                pBuffer.writeInt(pRecipe.getInputAmounts().get(i));
            }

            pBuffer.writeItemStack(pRecipe.getResultItem(null), false);

            pBuffer.writeInt(pRecipe.getExtraOutputs().size());
            for (ItemStack extraResult : pRecipe.getExtraOutputs()) {
                pBuffer.writeItemStack(extraResult, false);
            }
        }
    }
}
