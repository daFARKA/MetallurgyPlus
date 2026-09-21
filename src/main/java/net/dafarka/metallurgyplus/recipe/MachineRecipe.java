package net.dafarka.metallurgyplus.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class MachineRecipe implements Recipe<SimpleContainer> {

    public final RecipeType<? extends MachineRecipe> type;
    public final RecipeSerializer<?> serializer;

    public final NonNullList<Ingredient> inputItems;
    public final NonNullList<Integer> inputAmounts;
    public final ItemStack output;
    public final ResourceLocation id;

    public MachineRecipe(RecipeType<? extends MachineRecipe> type, RecipeSerializer<?> serializer, NonNullList<Ingredient> inputItems, NonNullList<Integer> inputAmounts, ItemStack output, ResourceLocation id) {
        this.type = type;
        this.serializer = serializer;
        this.inputItems = inputItems;
        this.inputAmounts = inputAmounts;
        this.output = output;
        this.id = id;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        if (level.isClientSide) {
            return false;
        }

        int inputItemValidityCount = 0;

        for (Ingredient ingredient : inputItems) {
            for (int j = 0; j < container.getContainerSize(); j++) {
                if (ingredient.test(container.getItem(j))) {
                    inputItemValidityCount++;
                    break;
                }
            }
        }

        return inputItemValidityCount == inputItems.size();
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
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
        int index = findIngredientIndex(inputItems, ingredient);

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
        return serializer;
    }

    @Override
    public RecipeType<?> getType() {
        return type;
    }

    private static int findIngredientIndex(NonNullList<Ingredient> ingredients, Ingredient target) {
        for (int i = 0; i < ingredients.size(); i++) {
            if (ingredients.get(i).test(target.getItems()[0])) {
                return i;
            }
        }
        return -1;
    }
}