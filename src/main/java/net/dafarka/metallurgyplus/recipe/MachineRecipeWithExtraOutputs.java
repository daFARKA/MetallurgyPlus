package net.dafarka.metallurgyplus.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class MachineRecipeWithExtraOutputs extends MachineRecipe {

    protected final NonNullList<ItemStack> extraOutputs;
    protected final NonNullList<Double> extraOutputChances;

    public MachineRecipeWithExtraOutputs(
        RecipeType<MachineRecipeWithExtraOutputs> type,
        RecipeSerializer<MachineRecipeWithExtraOutputs> serializer,
        NonNullList<Ingredient> inputItems,
        NonNullList<Integer> inputAmounts,
        ItemStack output,
        NonNullList<ItemStack> extraOutputs,
        NonNullList<Double> extraOutputChances,
        ResourceLocation id
    ) {
        super(type, serializer, inputItems, inputAmounts, output, id);

        this.extraOutputs = extraOutputs;
        this.extraOutputChances = extraOutputChances;
    }

    public NonNullList<ItemStack> getExtraOutputs() {
        return extraOutputs;
    }

    public NonNullList<Double> getExtraOutputChances() {
        return extraOutputChances;
    }
}