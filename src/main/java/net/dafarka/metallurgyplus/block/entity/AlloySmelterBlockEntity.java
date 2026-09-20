package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.recipe.AlloySmelterRecipe;
import net.dafarka.metallurgyplus.screen.menu.AlloySmelterMenu;
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

import java.util.Map;
import java.util.Optional;

public class AlloySmelterBlockEntity extends MachineBlockEntity {

    private final int INPUT_SLOT_COUNT = AlloySmelterMenu.INPUT_POSITIONS.length;
    private int outputSlot;


    public AlloySmelterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ALLOY_SMELTER_BE.get(), pPos, pBlockState, AlloySmelterMenu.INPUT_POSITIONS.length, AlloySmelterMenu.OUTPUT_POSITIONS.length);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.metallurgyplus.alloy_smelter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new AlloySmelterMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    protected boolean hasRecipe() {
        Optional<AlloySmelterRecipe> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        NonNullList<Ingredient> ingredients = recipe.get().getIngredients();
        NonNullList<Integer> amounts = recipe.get().getInputAmounts();

        for (int i = 0; i < ingredients.size(); i++) {
            if (utilBlockEntity.getFirstSlotThatContainsAnyOfInputItems(ingredients.get(i), 0, 8) == -1) return false;

            int count = 0;
            Map<Integer, Integer> counts = utilBlockEntity.getSlotIndexAndCountThatContainAnyOfInputItems(ingredients.get(i), 0, 8);
            for (int c : counts.values()) {
                count += c;
            }

            if (count < amounts.get(i)) return false;
        }

        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());
        outputSlot = utilBlockEntity.getFirstAvailableSlot(result.getItem(), result.getCount(), 8);

        return outputSlot != -1;
    }

    private Optional<AlloySmelterRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(INPUT_SLOT_COUNT);
        for (int i = 0; i < INPUT_SLOT_COUNT; i++) {
            inventory.setItem(i, this.allHandler.getStackInSlot(i));
        }

        return this.level.getRecipeManager().getRecipeFor(AlloySmelterRecipe.Type.INSTANCE, inventory, level);
    }

    protected void craftItem() {
        Optional<AlloySmelterRecipe> recipe = getCurrentRecipe();
        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());

        NonNullList<Ingredient> ingredients = recipe.get().getIngredients();
        NonNullList<Integer> amounts = recipe.get().getInputAmounts();
        for (int i = 0; i < ingredients.size(); i++) {
            if (utilBlockEntity.getFirstSlotThatContainsAnyOfInputItems(ingredients.get(i), 0, 8) == -1) return;

            Map<Integer, Integer> counts = utilBlockEntity.getSlotIndexAndCountThatContainAnyOfInputItems(ingredients.get(i), 0, 8);
            int remainder = amounts.get(i);
            for (int slotIndex : counts.keySet()) {
                if (remainder < counts.get(slotIndex)) {
                    this.allHandler.extractItem(slotIndex, remainder, false);
                    break;
                }

                this.allHandler.extractItem(slotIndex, counts.get(slotIndex), false);
                remainder -= counts.get(slotIndex);
            }
        }

        this.allHandler.setStackInSlot(outputSlot, new ItemStack(result.getItem(),
            this.allHandler.getStackInSlot(outputSlot).getCount() + result.getCount()));
    }
}
