package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.recipe.ExtractorRecipe;
import net.dafarka.metallurgyplus.screen.menu.ExtractorMenu;
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
import java.util.Random;

public class ExtractorBlockEntity extends MachineBlockEntity {

    private final int INPUT_SLOT_COUNT = ExtractorMenu.INPUT_POSITIONS.length;

    private int outputSlot;

    private final Random random;

    public ExtractorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ETRACTOR_BE.get(), pPos, pBlockState, ExtractorMenu.INPUT_POSITIONS.length, ExtractorMenu.OUTPUT_POSITIONS.length);
        random = new Random();
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.metallurgyplus.extractor");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ExtractorMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    protected boolean hasRecipe() {
        Optional<ExtractorRecipe> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        NonNullList<Ingredient> ingredients = recipe.get().getIngredients();
        NonNullList<Integer> amounts = recipe.get().getInputAmounts();
        for (int i = 0; i < ingredients.size(); i++) {
            if (utilBlockEntity.getFirstSlotThatContainsAnyOfInputItems(ingredients.get(i), 0, 4) == -1) return false;

            int count = 0;
            Map<Integer, Integer> counts = utilBlockEntity.getSlotIndexAndCountThatContainAnyOfInputItems(ingredients.get(i), 0, 4);
            for (int c : counts.values()) {
                count += c;
            }

            if (count < amounts.get(i)) return false;
        }

        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());
        NonNullList<ItemStack> extraOutputs = recipe.get().getExtraOutputs();
        if (extraOutputs != null) {
            for (ItemStack currentItemStack : extraOutputs) {
                outputSlot = utilBlockEntity.getFirstAvailableSlot(currentItemStack.getItem(), currentItemStack.getCount(), 4);
                if (outputSlot == -1) {
                    return false;
                }
            }
        }
        outputSlot = utilBlockEntity.getFirstAvailableSlot(result.getItem(), result.getCount(), 4);

        return outputSlot != -1;
    }

    private Optional<ExtractorRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(INPUT_SLOT_COUNT);
        for (int i = 0; i < INPUT_SLOT_COUNT; i++) {
            inventory.setItem(i, this.allHandler.getStackInSlot(i));
        }

        return this.level.getRecipeManager().getRecipeFor(ExtractorRecipe.Type.INSTANCE, inventory, level);
    }

    protected void craftItem() {
        Optional<ExtractorRecipe> recipe = getCurrentRecipe();
        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());

        NonNullList<Ingredient> ingredients = recipe.get().getIngredients();
        NonNullList<Integer> amounts = recipe.get().getInputAmounts();
        for (int i = 0; i < ingredients.size(); i++) {
            if (utilBlockEntity.getFirstSlotThatContainsAnyOfInputItems(ingredients.get(i), 0, 4) == -1) return;

            Map<Integer, Integer> counts = utilBlockEntity.getSlotIndexAndCountThatContainAnyOfInputItems(ingredients.get(i), 0, 4);
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

        NonNullList<ItemStack> extraOutputs = recipe.get().getExtraOutputs();
        NonNullList<Double> extraOutputChances = recipe.get().getExtraOutputChances();
        if (extraOutputs != null) {
            int i = 0;
            for (ItemStack currentItemStack : extraOutputs) {
                boolean success = true;
                double chance = extraOutputChances.get(i);
                if (chance < 1.0) {
                    success = random.nextDouble() < chance;
                }
                if (success) {
                    outputSlot = utilBlockEntity.getFirstAvailableSlot(currentItemStack.getItem(), currentItemStack.getCount(), 4);
                    this.allHandler.setStackInSlot(outputSlot, new ItemStack(currentItemStack.getItem(),
                        this.allHandler.getStackInSlot(outputSlot).getCount() + currentItemStack.getCount()));
                }
                i++;
            }
        }
    }
}
