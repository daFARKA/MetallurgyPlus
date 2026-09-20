package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.block.GenericEnergyStorage;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MachineBlockEntity extends BlockEntity implements MenuProvider {

    protected int progress = 0;
    protected int maxProgress = 100;

    protected final GenericEnergyStorage energyStorage =
        new GenericEnergyStorage(ModBlocks.ENERGY_CAPACITY, ModBlocks.ENERGY_MAX_RECEIVE, ModBlocks.ENERGY_MAX_EXTRACT);

    protected final ItemStackHandler inputHandler;
    protected final ItemStackHandler outputHandler;
    protected final CombinedInvWrapper allHandler;
    protected final UtilBlockEntity utilBlockEntity;

    protected LazyOptional<GenericEnergyStorage> energyLazy = LazyOptional.empty();
    protected LazyOptional<IItemHandler> inputLazy = LazyOptional.empty();
    protected LazyOptional<IItemHandler> outputLazy = LazyOptional.empty();
    protected LazyOptional<IItemHandler> allLazy = LazyOptional.empty();

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> maxProgress;
                case 2 -> energyStorage.getEnergyStored();
                case 3 -> energyStorage.getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 1 -> maxProgress = value;
                case 2 -> energyStorage.receiveEnergy(value, false);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    protected MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int inputSlots, int outputSlots) {
        super(type, pos, state);

        inputHandler = new ItemStackHandler(inputSlots) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };

        outputHandler = new ItemStackHandler(outputSlots) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return false;
            }
        };

        allHandler = new CombinedInvWrapper(inputHandler, outputHandler);
        utilBlockEntity = new UtilBlockEntity(allHandler);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == Direction.UP) {
                return inputLazy.cast();
            } else if (side == Direction.DOWN) {
                return outputLazy.cast();
            } else if (side != null) {
                return inputLazy.cast();
            } else {
                return allLazy.cast();
            }
        }

        if (cap == ForgeCapabilities.ENERGY) {
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
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put("input", inputHandler.serializeNBT());
        tag.put("output", outputHandler.serializeNBT());
        tag.putInt("progress", progress);
        tag.put("energy", energyStorage.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        inputHandler.deserializeNBT(tag.getCompound("input"));
        outputHandler.deserializeNBT(tag.getCompound("output"));
        progress = tag.getInt("progress");
        energyStorage.deserializeNBT(tag.getCompound("energy"));
    }

    @Override
    public abstract Component getDisplayName();

    @Nullable
    @Override
    public abstract AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player);

    public void resetProgress() {
        progress = 0;
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (hasRecipe() && energyStorage.getEnergyStored() >= ModBlocks.ENERGY_CONSUMPTION_PER_TICK) {
            energyStorage.extractEnergy(ModBlocks.ENERGY_CONSUMPTION_PER_TICK, false);
            progress++;
            setChanged(pLevel, pPos, pState);

            if (progress >= maxProgress) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    protected abstract boolean hasRecipe();

    protected abstract void craftItem();


}