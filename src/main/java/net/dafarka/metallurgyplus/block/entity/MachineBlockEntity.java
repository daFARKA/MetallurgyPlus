package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.block.GenericEnergyStorage;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.custom.MachineBlock;
import net.dafarka.metallurgyplus.recipe.MachineRecipe;
import net.dafarka.metallurgyplus.recipe.MachineRecipeWithExtraOutputs;
import net.dafarka.metallurgyplus.screen.menu.MachineMenu;
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
import net.minecraft.world.item.crafting.RecipeType;
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

public class MachineBlockEntity extends BlockEntity implements MenuProvider {

    private MachineBlock machineBlock;

    protected int progress = 0;
    protected int maxProgress = 100;

    protected int outputSlot = -1;

    protected final Random random = new Random();

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

    public MachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MACHINE_BE.get(), pos, state);

        this.machineBlock = (MachineBlock) state.getBlock();

        inputHandler = new ItemStackHandler(machineBlock.getInputSlots()) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };

        outputHandler = new ItemStackHandler(machineBlock.getOutputSlots()) {
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

    private MachineBlock getMachineBlock() {
        return machineBlock;
    }

    protected RecipeType<? extends MachineRecipe> getRecipeType() {
        return getMachineBlock().getRecipeType();
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
    public Component getDisplayName() {
        return Component.translatable(
            getMachineBlock().getDescriptionId()
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MachineMenu(containerId, playerInventory, this, data);
    }

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

    protected boolean hasRecipe() {
        Optional<? extends MachineRecipe> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        MachineRecipe currentRecipe = recipe.get();

        NonNullList<Ingredient> ingredients = currentRecipe.getIngredients();
        NonNullList<Integer> amounts = currentRecipe.getInputAmounts();

        SimpleContainer inventory = new SimpleContainer(inputHandler.getSlots());

        for (int i = 0; i < inputHandler.getSlots(); i++) {
            inventory.setItem(i, inputHandler.getStackInSlot(i).copy());
        }

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            int requiredAmount = amounts.get(i);

            int remainingAmount = requiredAmount;

            for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                ItemStack stack = inventory.getItem(slot);

                if (!ingredient.test(stack)) {
                    continue;
                }

                int amount = Math.min(
                    remainingAmount,
                    stack.getCount()
                );

                remainingAmount -= amount;

                stack.shrink(amount);

                if (remainingAmount <= 0) {
                    break;
                }
            }

            if (remainingAmount > 0) {
                return false;
            }
        }

        ItemStack result = currentRecipe.getResultItem(getLevel().registryAccess());

        outputSlot = utilBlockEntity.getFirstAvailableSlot(result.getItem(), result.getCount(), machineBlock.getInputSlots());

        if (outputSlot == -1) {
            return false;
        }

        if (currentRecipe instanceof MachineRecipeWithExtraOutputs extraRecipe) {
            NonNullList<ItemStack> extraOutputs =
                extraRecipe.getExtraOutputs();

            if (extraOutputs != null) {
                for (ItemStack extraOutput : extraOutputs) {
                    if (utilBlockEntity.getFirstAvailableSlot(extraOutput.getItem(), extraOutput.getCount(), machineBlock.getInputSlots()) == -1) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    protected Optional<? extends MachineRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(inputHandler.getSlots());

        for (int i = 0; i < inputHandler.getSlots(); i++) {
            inventory.setItem(i, inputHandler.getStackInSlot(i));
        }

        return level.getRecipeManager().getRecipeFor(getRecipeType(), inventory, level);
    }

    protected void craftItem() {
        Optional<? extends MachineRecipe> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return;
        }

        MachineRecipe currentRecipe = recipe.get();

        NonNullList<Ingredient> ingredients = currentRecipe.getIngredients();
        NonNullList<Integer> amounts = currentRecipe.getInputAmounts();

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            int remainingAmount = amounts.get(i);

            for (int slot = 0; slot < inputHandler.getSlots(); slot++) {
                ItemStack stack = inputHandler.getStackInSlot(slot);

                if (!ingredient.test(stack)) {
                    continue;
                }

                int amount = Math.min(remainingAmount, stack.getCount());

                inputHandler.extractItem(slot, amount, false);

                remainingAmount -= amount;

                if (remainingAmount <= 0) {
                    break;
                }
            }

            if (remainingAmount > 0) {
                return;
            }
        }

        ItemStack result = currentRecipe.getResultItem(getLevel().registryAccess());

        allHandler.setStackInSlot(
            outputSlot,
            new ItemStack(
                result.getItem(),
                allHandler.getStackInSlot(outputSlot).getCount()
                    + result.getCount()
            )
        );

        if (currentRecipe instanceof MachineRecipeWithExtraOutputs extraRecipe) {
            NonNullList<ItemStack> extraOutputs = extraRecipe.getExtraOutputs();

            NonNullList<Double> extraOutputChances = extraRecipe.getExtraOutputChances();

            if (extraOutputs != null) {
                for (int i = 0; i < extraOutputs.size(); i++) {
                    ItemStack extraOutput = extraOutputs.get(i);

                    double chance = extraOutputChances.get(i);

                    if (random.nextDouble() >= chance) {
                        continue;
                    }

                    int extraOutputSlot = utilBlockEntity.getFirstAvailableSlot(extraOutput.getItem(), extraOutput.getCount(), machineBlock.getInputSlots());

                    if (extraOutputSlot == -1) {
                        continue;
                    }

                    allHandler.setStackInSlot(
                        extraOutputSlot,
                        new ItemStack(
                            extraOutput.getItem(),
                            allHandler
                                .getStackInSlot(extraOutputSlot)
                                .getCount()
                                + extraOutput.getCount()
                        )
                    );
                }
            }
        }
    }
}