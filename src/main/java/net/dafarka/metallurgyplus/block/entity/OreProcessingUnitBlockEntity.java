package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.block.GenericEnergyStorage;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.recipe.OreProcessingUnitRecipe;
import net.dafarka.metallurgyplus.screen.menu.OreProcessingUnitMenu;
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
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

public class OreProcessingUnitBlockEntity extends BlockEntity implements MenuProvider {

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;

    private final int INPUT_SLOT_COUNT = 1;
    private int outputSlot;

    private Random random;

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(ModBlocks.ENERGY_CAPACITY, ModBlocks.ENERGY_MAX_RECIEVE, 0);
    private LazyOptional<GenericEnergyStorage> energyLazy = LazyOptional.empty();

    public OreProcessingUnitBlockEntity(BlockPos pPos,
                                        BlockState pBlockState) {
        super(ModBlockEntities.ORE_PROCESSING_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> OreProcessingUnitBlockEntity.this.progress;
                    case 1 -> OreProcessingUnitBlockEntity.this.maxProgress;
                    case 2 -> OreProcessingUnitBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> OreProcessingUnitBlockEntity.this.energyStorage.getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> OreProcessingUnitBlockEntity.this.progress = pValue;
                    case 1 -> OreProcessingUnitBlockEntity.this.maxProgress = pValue;
                    case 2 -> OreProcessingUnitBlockEntity.this.energyStorage.receiveEnergy(pValue, false);
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };

        random = new Random();
    }

    // --- Slot Groups ---
    private final ItemStackHandler inputHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final ItemStackHandler outputHandler = new ItemStackHandler(18) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return false;
        }
    };

    CombinedInvWrapper allHandler = new CombinedInvWrapper(inputHandler, outputHandler);
    private UtilBlockEntity utilBlockEntity = new UtilBlockEntity(allHandler);

    private LazyOptional<IItemHandler> inputLazy = LazyOptional.empty();
    private LazyOptional<IItemHandler> outputLazy = LazyOptional.empty();
    private LazyOptional<IItemHandler> allLazy = LazyOptional.empty();

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == Direction.UP) {
                // Hopper above -> Input 1
                return inputLazy.cast();
            } else if (side == Direction.DOWN) {
                // Hopper below -> Output
                return outputLazy.cast();
            } else if (side != null) {
                // Hopper from side -> Input 2
                return inputLazy.cast();
            } else {
                // Default (GUI)
                return allLazy.cast();
            }
        } else if (cap == ForgeCapabilities.ENERGY) { // accept energy from all sides
            return energyLazy.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        inputLazy = LazyOptional.of(() -> inputHandler);
        outputLazy = LazyOptional.of(() -> outputHandler);
        allLazy = LazyOptional.of(() -> allHandler);

        energyLazy = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inputLazy.invalidate();
        outputLazy.invalidate();
        allLazy.invalidate();

        energyLazy.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(allHandler.getSlots());
        for (int i = 0; i < allHandler.getSlots(); i++) {
            inventory.setItem(i, allHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.metallurgyplus.ore_processing_unit");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new OreProcessingUnitMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("input", inputHandler.serializeNBT());
        pTag.put("output", outputHandler.serializeNBT());
        pTag.putInt("progress", progress);

        pTag.put("energy", energyStorage.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        inputHandler.deserializeNBT(pTag.getCompound("input"));
        outputHandler.deserializeNBT(pTag.getCompound("output"));
        progress = pTag.getInt("progress");

        energyStorage.deserializeNBT(pTag.getCompound("energy"));
    }


    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if(hasRecipe() && energyStorage.getEnergyStored() >= ModBlocks.ENERGY_CONSUMPTION_PER_TICK) {
            energyStorage.extractEnergy(ModBlocks.ENERGY_CONSUMPTION_PER_TICK, false);
            progress++;
            setChanged(pLevel, pPos, pState);

            if(progress >= maxProgress) {
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

    private boolean hasRecipe() {
        Optional<OreProcessingUnitRecipe> recipe = getCurrentRecipe();

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
        NonNullList<ItemStack> extraOutputs = recipe.get().getExtraOutputs();
        if (extraOutputs != null) {
            for (ItemStack currentItemStack : extraOutputs) {
                outputSlot = utilBlockEntity.getFirstAvailableSlot(currentItemStack.getItem(), currentItemStack.getCount(), 1);
                if (outputSlot == -1) {
                    return false;
                }
            }
        }
        outputSlot = utilBlockEntity.getFirstAvailableSlot(result.getItem(), result.getCount(), 1);

        return outputSlot != -1;
    }

    private Optional<OreProcessingUnitRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(INPUT_SLOT_COUNT);
        for (int i = 0; i < INPUT_SLOT_COUNT; i++) {
            inventory.setItem(i, this.allHandler.getStackInSlot(i));
        }

        return this.level.getRecipeManager().getRecipeFor(OreProcessingUnitRecipe.Type.INSTANCE, inventory, level);
    }

    private void craftItem() {
        Optional<OreProcessingUnitRecipe> recipe = getCurrentRecipe();
        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());

        NonNullList<Ingredient> ingredients = recipe.get().getIngredients();
        NonNullList<Integer> amounts = recipe.get().getInputAmounts();
        for (int i = 0; i < ingredients.size(); i++) {
            if (utilBlockEntity.getFirstSlotThatContainsAnyOfInputItems(ingredients.get(i), 0, 0) == -1) return;
            this.allHandler.extractItem(utilBlockEntity.getFirstSlotThatContainsAnyOfInputItems(ingredients.get(i), 0, 0), amounts.get(i), false);
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
                    success =  random.nextDouble() < chance;
                }
                if (success) {
                    outputSlot = utilBlockEntity.getFirstAvailableSlot(currentItemStack.getItem(), currentItemStack.getCount(), 1);
                    this.allHandler.setStackInSlot(outputSlot, new ItemStack(currentItemStack.getItem(),
                        this.allHandler.getStackInSlot(outputSlot).getCount() + currentItemStack.getCount()));
                }
                i++;
            }
        }
    }
}
