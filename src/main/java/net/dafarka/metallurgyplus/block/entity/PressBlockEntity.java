package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.recipe.PressRecipe;
import net.dafarka.metallurgyplus.screen.menu.PressMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PressBlockEntity extends MachineBlockEntity {

    private static final int INPUT_SLOT_COUNT = 1;

    private int outputSlot;

    public PressBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.PRESS_BE.get(), pPos, pBlockState, 1, PressMenu.OUTPUT_POSITIONS.length);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.metallurgyplus.press");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new PressMenu(pContainerId, pPlayerInventory, this, this.data);
    }


    protected boolean hasRecipe() {
        Optional<PressRecipe> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        NonNullList<Ingredient> ingredients = recipe.get().getIngredients();

        NonNullList<Integer> amounts = recipe.get().getInputAmounts();

        for (int i = 0; i < ingredients.size(); i++) {
            int slot = utilBlockEntity.getFirstSlotThatContainsAnyOfInputItems(ingredients.get(i), 0, 0);

            if (slot == -1) {
                return false;
            }

            if (allHandler.getStackInSlot(slot).getCount() < amounts.get(i)) {
                return false;
            }
        }

        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());

        outputSlot = utilBlockEntity.getFirstAvailableSlot(result.getItem(), result.getCount(), 1);

        return outputSlot != -1;
    }

    private Optional<PressRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(INPUT_SLOT_COUNT);

        for (int i = 0; i < INPUT_SLOT_COUNT; i++) {
            inventory.setItem(i, allHandler.getStackInSlot(i));
        }

        return level.getRecipeManager().getRecipeFor(PressRecipe.Type.INSTANCE, inventory, level);
    }

    protected void craftItem() {
        Optional<PressRecipe> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) {
            return;
        }

        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());

        NonNullList<Ingredient> ingredients = recipe.get().getIngredients();

        NonNullList<Integer> amounts = recipe.get().getInputAmounts();

        for (int i = 0; i < ingredients.size(); i++) {
            int slot = utilBlockEntity.getFirstSlotThatContainsAnyOfInputItems(ingredients.get(i), 0, 0);

            if (slot == -1) {
                return;
            }

            allHandler.extractItem(slot, amounts.get(i), false);
        }

        allHandler.setStackInSlot(outputSlot, new ItemStack(result.getItem(),
            allHandler.getStackInSlot(outputSlot).getCount() + result.getCount()));
    }
}