package net.dafarka.metallurgyplus.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nullable;

public class MachineFinishedRecipeWithExtraOutputs extends MachineFinishedRecipe {

    private final NonNullList<ItemStack> extraOutputs;
    private final NonNullList<Float> extraChances;

    public MachineFinishedRecipeWithExtraOutputs(
        NonNullList<Item> inputItems,
        NonNullList<Integer> inputAmounts,
        ItemStack output,
        @Nullable NonNullList<ItemStack> extraOutputs,
        @Nullable NonNullList<Float> extraChances,
        ResourceLocation id,
        String recipeType,
        RecipeSerializer<?> serializer
    ) {
        super(
            inputItems,
            inputAmounts,
            output,
            id,
            recipeType,
            serializer
        );

        this.extraOutputs = extraOutputs;
        this.extraChances = extraChances;
    }

    @Override
    public void serializeRecipeData(JsonObject pJson) {
        super.serializeRecipeData(pJson);

        JsonArray extraOutputArray = new JsonArray();
        int i = 0;

        if (extraOutputs != null) {
            for (ItemStack extraOutput : extraOutputs) {
                JsonObject ingredientObj = new JsonObject();

                ingredientObj.addProperty("item", Utility.formatResourceName(extraOutput.getItem().getDescriptionId()));

                ingredientObj.addProperty("count", extraOutput.getCount());

                ingredientObj.addProperty("chance", extraChances.get(i));

                extraOutputArray.add(ingredientObj);
                i++;
            }

            pJson.add("extra_outputs", extraOutputArray);
        }
    }
}
