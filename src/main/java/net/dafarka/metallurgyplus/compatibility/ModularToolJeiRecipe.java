package net.dafarka.metallurgyplus.compatibility;

import net.minecraft.world.item.ItemStack;

import java.util.List;

/** A JEI-only example of one modular-tool crafting operation. */
public final class ModularToolJeiRecipe {
    private final List<ItemStack> inputSlots;
    private final ItemStack output;

    public ModularToolJeiRecipe(List<ItemStack> inputSlots, ItemStack output) {
        if (inputSlots.size() != 9) {
            throw new IllegalArgumentException("A modular-tool JEI recipe must have 9 crafting slots");
        }
        this.inputSlots = inputSlots.stream().map(ItemStack::copy).toList();
        this.output = output.copy();
    }

    public ItemStack getInput(int slot) {
        return inputSlots.get(slot).copy();
    }

    public ItemStack getOutput() {
        return output.copy();
    }
}
