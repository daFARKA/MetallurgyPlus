package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.recipe.AlloySmelterRecipe;
import net.dafarka.metallurgyplus.recipe.OreProcessingUnitRecipe;
import net.dafarka.metallurgyplus.screen.AlloySmelterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AlloySmelterBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(MetallurgyPlus.ALLOY_SMELTER_SLOTS_COUNT);

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;

    private UtilBlockEntity utilBlockEntity = new UtilBlockEntity(this.itemHandler);

    private final int INPUT_SLOT_COUNT = 8;
    private int outputSlot;

    public AlloySmelterBlockEntity(BlockPos pPos,
                                   BlockState pBlockState) {
        super(ModBlockEntities.ALLOY_SMELTER_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> AlloySmelterBlockEntity.this.progress;
                    case 1 -> AlloySmelterBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> AlloySmelterBlockEntity.this.progress = pValue;
                    case 1 -> AlloySmelterBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.metallurgyplus.alloy_smelter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new AlloySmelterMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("alloy_smelter.progress", progress);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("alloy_smelter.progress");
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if(hasRecipe()) {
            increaseCraftingProgress();
            setChanged(pLevel, pPos, pState);

            if(hasProgressFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean hasRecipe() {
        Optional<AlloySmelterRecipe> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        NonNullList<Ingredient> ingredients = recipe.get().getIngredientsCustom();
        NonNullList<Integer> amounts = recipe.get().getInputAmounts();
        for (int i = 0; i < ingredients.size(); i++) {
            if (utilBlockEntity.getFirstSlotThatContainsAnyOfInputItems(ingredients.get(i), 0, 8) == -1) return false;
            if (itemHandler.getStackInSlot(utilBlockEntity.getFirstSlotThatContainsAnyOfInputItems(ingredients.get(i), 0, 8)).getCount() < amounts.get(i)) {
                return false;
            }
        }

        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());
        outputSlot = utilBlockEntity.getFirstAvailableSlot(result.getItem(), result.getCount(), 8);

        return outputSlot != -1;
    }

    private Optional<AlloySmelterRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(INPUT_SLOT_COUNT);
        for (int i = 0; i < INPUT_SLOT_COUNT; i++) {
            inventory.setItem(i, this.itemHandler.getStackInSlot(i));
        }

        return this.level.getRecipeManager().getRecipeFor(AlloySmelterRecipe.Type.INSTANCE, inventory, level);
    }

    private void craftItem() {
        Optional<AlloySmelterRecipe> recipe = getCurrentRecipe();
        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());

        NonNullList<Ingredient> ingredients = recipe.get().getIngredientsCustom();
        NonNullList<Integer> amounts = recipe.get().getInputAmounts();
        for (int i = 0; i < ingredients.size(); i++) {
            if (utilBlockEntity.getFirstSlotThatContainsAnyOfInputItems(ingredients.get(i), 0, 8) == -1) return;
            this.itemHandler.extractItem(utilBlockEntity.getFirstSlotThatContainsAnyOfInputItems(ingredients.get(i), 0, 8), amounts.get(i), false);
        }

        this.itemHandler.setStackInSlot(outputSlot, new ItemStack(result.getItem(),
            this.itemHandler.getStackInSlot(outputSlot).getCount() + result.getCount()));
    }
}
