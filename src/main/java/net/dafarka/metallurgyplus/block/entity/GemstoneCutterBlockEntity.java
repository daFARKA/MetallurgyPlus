package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.recipe.GemstoneCutterRecipe;
import net.dafarka.metallurgyplus.screen.menu.GemstoneCutterMenu;
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

public class GemstoneCutterBlockEntity extends MachineBlockEntity {
    private final int INPUT_SLOT_COUNT = 1;
    private int outputSlot;


    public GemstoneCutterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.GEMSTONE_CUTTER_BE.get(), pPos, pBlockState, 1, 9);
    }


    @NotNull
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.metallurgyplus.gemstone_cutter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new GemstoneCutterMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    protected boolean hasRecipe() {
        Optional<GemstoneCutterRecipe> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        NonNullList<Ingredient> ingredients = recipe.get().getIngredients();
        NonNullList<Integer> amounts = recipe.get().getInputAmounts();
        for (int i = 0; i < ingredients.size(); i++) {
            if (utilBlockEntity.getFirstSlotThatContainsAnyOfInputItems(ingredients.get(i), 0, 0) == -1) return false;
            if (allHandler.getStackInSlot(utilBlockEntity.getFirstSlotThatContainsAnyOfInputItems(ingredients.get(i), 0, 0)).getCount() < amounts.get(i)) {
                return false;
            }
        }

        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());
        outputSlot = utilBlockEntity.getFirstAvailableSlot(result.getItem(), result.getCount(), 1);

        return outputSlot != -1;
    }

    private Optional<GemstoneCutterRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(INPUT_SLOT_COUNT);
        for (int i = 0; i < INPUT_SLOT_COUNT; i++) {
            inventory.setItem(i, this.allHandler.getStackInSlot(i));
        }

        return this.level.getRecipeManager().getRecipeFor(GemstoneCutterRecipe.Type.INSTANCE, inventory, level);
    }

    protected void craftItem() {
        Optional<GemstoneCutterRecipe> recipe = getCurrentRecipe();
        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());

        NonNullList<Ingredient> ingredients = recipe.get().getIngredients();
        NonNullList<Integer> amounts = recipe.get().getInputAmounts();
        for (int i = 0; i < ingredients.size(); i++) {
            if (utilBlockEntity.getFirstSlotThatContainsAnyOfInputItems(ingredients.get(i), 0, 0) == -1) return;
            this.allHandler.extractItem(utilBlockEntity.getFirstSlotThatContainsAnyOfInputItems(ingredients.get(i), 0, 0), amounts.get(i), false);
        }

        this.allHandler.setStackInSlot(outputSlot, new ItemStack(result.getItem(),
            this.allHandler.getStackInSlot(outputSlot).getCount() + result.getCount()));
    }
}
