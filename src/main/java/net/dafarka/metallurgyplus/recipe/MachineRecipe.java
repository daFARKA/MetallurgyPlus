package net.dafarka.metallurgyplus.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public abstract class MachineRecipe implements Recipe<SimpleContainer> {

    protected final NonNullList<Ingredient> inputItems;
    protected final NonNullList<Integer> inputAmounts;
    protected final ItemStack output;
    protected final ResourceLocation id;

    protected MachineRecipe(NonNullList<Ingredient> inputItems, NonNullList<Integer> inputAmounts, ItemStack output, ResourceLocation id) {
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
        int index = UtilRecipe.findIngredientIndex(
            inputItems,
            ingredient
        );

        if (index != -1) {
            return inputAmounts.get(index);
        }

        return 1;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }
}