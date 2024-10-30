package net.dafarka.metallurgyplus.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;

public class UtilRecipe {


    /**
     * Finds the first index of an ingredient.
     *
     * Loops through all the ingredients and tests whether it is the target ingredient.
     *
     * @param ingredients The NonNullList of Ingredients
     * @param target The target ingredient
     *
     * @return the index or -1 if not found (should never happen though)
     * */
    public int findIngredientIndex(NonNullList<Ingredient> ingredients, Ingredient target) {
        for (int i = 0; i < ingredients.size(); i++) {
            if (ingredients.get(i).test(target.getItems()[0])) {
                return i;
            }
        }
        return -1;
    }
}
