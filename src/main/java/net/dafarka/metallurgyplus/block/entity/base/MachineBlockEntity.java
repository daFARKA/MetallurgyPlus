package net.dafarka.metallurgyplus.block.entity.base;

import net.dafarka.metallurgyplus.block.BlockFace;
import net.dafarka.metallurgyplus.block.ItemSideMode;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.base.MachineBlock;
import net.dafarka.metallurgyplus.block.entity.ModBlockEntities;
import net.dafarka.metallurgyplus.energy.GenericEnergyStorage;
import net.dafarka.metallurgyplus.recipe.MachineRecipe;
import net.dafarka.metallurgyplus.recipe.MachineRecipeWithExtraOutputs;
import net.dafarka.metallurgyplus.screen.menu.MachineMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
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
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class MachineBlockEntity extends EnergyBlockEntity<GenericEnergyStorage> implements MenuProvider {

    private static final int AUTO_IO_TRANSFER_INTERVAL = 20;

    private MachineBlock machineBlock;

    private final EnumMap<BlockFace, ItemSideMode> itemSideModes = new EnumMap<>(BlockFace.class);
    private boolean autoInputEnabled;
    private boolean autoOutputEnabled;

    protected int progress = 0;
    protected int maxProgress = 100;

    protected int outputSlot = -1;

    protected final Random random = new Random();

    protected final ItemStackHandler inputHandler;
    protected final ItemStackHandler outputHandler;
    protected final CombinedInvWrapper allHandler;
    private final IItemHandler inputOnlyHandler;
    private int autoIOTransferCooldown;

    private final EnumMap<Direction, LazyOptional<IItemHandler>> sideItemHandlerCapabilities = new EnumMap<>(Direction.class);
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
        super(
            ModBlockEntities.MACHINE_BE.get(),
            pos,
            state,
            ModBlocks.ENERGY_CAPACITY,
            ModBlocks.ENERGY_MAX_RECEIVE,
            ModBlocks.ENERGY_MAX_EXTRACT
        );

        this.machineBlock = (MachineBlock) state.getBlock();
        resetItemSideModesToDefaults();

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
        inputOnlyHandler = new IItemHandler() {
            @Override
            public int getSlots() {
                return inputHandler.getSlots();
            }

            @Override
            public @NotNull ItemStack getStackInSlot(int slot) {
                return inputHandler.getStackInSlot(slot);
            }

            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                return inputHandler.insertItem(slot, stack, simulate);
            }

            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }

            @Override
            public int getSlotLimit(int slot) {
                return inputHandler.getSlotLimit(slot);
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return inputHandler.isItemValid(slot, stack);
            }
        };
    }

    private MachineBlock getMachineBlock() {
        return machineBlock;
    }

    protected RecipeType<? extends MachineRecipe> getRecipeType() {
        return getMachineBlock().getRecipeType();
    }

    private int getFirstEmptyOutputSlot() {
        for (int slot = machineBlock.getInputSlots(); slot < allHandler.getSlots(); slot++) {
            if (allHandler.getStackInSlot(slot).isEmpty()) {
                return slot;
            }
        }

        return -1;
    }

    private int getFirstAvailableOutputSlot(Item item, int amount) {
        for (int slot = machineBlock.getInputSlots(); slot < allHandler.getSlots(); slot++) {
            ItemStack stack = allHandler.getStackInSlot(slot);
            if (stack.is(item) && stack.getCount() + amount <= stack.getMaxStackSize()) {
                return slot;
            }
        }

        return getFirstEmptyOutputSlot();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                return allLazy.cast();
            }

            return getSideItemHandlerCapability(side).cast();
        }

        return super.getCapability(cap, side);
    }

    private LazyOptional<IItemHandler> getSideItemHandlerCapability(Direction worldSide) {
        ItemSideMode mode = getItemSideMode(getBlockFace(worldSide));
        if (mode == ItemSideMode.DISABLED) {
            return LazyOptional.empty();
        }

        LazyOptional<IItemHandler> capability = sideItemHandlerCapabilities.get(worldSide);
        if (capability != null && capability.isPresent()) {
            return capability;
        }

        capability = switch (mode) {
            case INPUT -> LazyOptional.of(() -> inputOnlyHandler);
            case OUTPUT -> LazyOptional.of(() -> outputHandler);
            case BOTH -> LazyOptional.of(() -> allHandler);
            case DISABLED -> LazyOptional.empty();
        };
        if (capability.isPresent()) {
            sideItemHandlerCapabilities.put(worldSide, capability);
        }
        return capability;
    }

    private BlockFace getBlockFace(Direction worldSide) {
        Direction facing = getBlockState().getValue(MachineBlock.FACING);
        if (worldSide == Direction.UP) {
            return BlockFace.TOP;
        }
        if (worldSide == Direction.DOWN) {
            return BlockFace.BOTTOM;
        }
        if (worldSide == facing) {
            return BlockFace.FRONT;
        }
        if (worldSide == facing.getOpposite()) {
            return BlockFace.BACK;
        }
        if (worldSide == facing.getClockWise()) {
            return BlockFace.LEFT;
        }
        return BlockFace.RIGHT;
    }

    private Direction getWorldSide(BlockFace face) {
        Direction facing = getBlockState().getValue(MachineBlock.FACING);
        return switch (face) {
            case TOP -> Direction.UP;
            case BOTTOM -> Direction.DOWN;
            case FRONT -> facing;
            case BACK -> facing.getOpposite();
            case LEFT -> facing.getClockWise();
            case RIGHT -> facing.getCounterClockWise();
        };
    }

    @Override
    public void onLoad() {
        super.onLoad();

        allLazy = LazyOptional.of(() -> allHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        for (LazyOptional<IItemHandler> capability : sideItemHandlerCapabilities.values()) {
            capability.invalidate();
        }
        sideItemHandlerCapabilities.clear();
        allLazy.invalidate();
    }

    public ItemSideMode getItemSideMode(BlockFace face) {
        return itemSideModes.get(Objects.requireNonNull(face, "face"));
    }

    public boolean isAutoInputEnabled() {
        return autoInputEnabled;
    }

    public void setAutoInputEnabled(boolean enabled) {
        if (autoInputEnabled != enabled) {
            autoInputEnabled = enabled;
            setChanged();
        }
    }

    public boolean isAutoOutputEnabled() {
        return autoOutputEnabled;
    }

    public void setAutoOutputEnabled(boolean enabled) {
        if (autoOutputEnabled != enabled) {
            autoOutputEnabled = enabled;
            setChanged();
        }
    }

    public void setItemSideMode(BlockFace face, ItemSideMode mode) {
        Objects.requireNonNull(face, "face");
        Objects.requireNonNull(mode, "mode");

        if (itemSideModes.put(face, mode) != mode) {
            setChanged();

            Direction worldSide = getWorldSide(face);
            LazyOptional<IItemHandler> oldCapability = sideItemHandlerCapabilities.remove(worldSide);
            if (oldCapability != null) {
                oldCapability.invalidate();
            }

            if (level != null && !level.isClientSide) {
                level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
            }
        }
    }

    private void resetItemSideModesToDefaults() {
        itemSideModes.clear();
        for (BlockFace face : BlockFace.values()) {
            itemSideModes.put(face, face == BlockFace.BOTTOM ? ItemSideMode.OUTPUT : ItemSideMode.INPUT);
        }
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

        CompoundTag sideModesTag = new CompoundTag();
        itemSideModes.forEach((face, mode) -> sideModesTag.putString(face.name(), mode.name()));
        tag.put("item_side_modes", sideModesTag);
        tag.putBoolean("auto_input_enabled", autoInputEnabled);
        tag.putBoolean("auto_output_enabled", autoOutputEnabled);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        inputHandler.deserializeNBT(tag.getCompound("input"));
        outputHandler.deserializeNBT(tag.getCompound("output"));
        progress = tag.getInt("progress");

        resetItemSideModesToDefaults();
        CompoundTag sideModesTag = tag.getCompound("item_side_modes");
        for (BlockFace face : BlockFace.values()) {
            if (!sideModesTag.contains(face.name(), Tag.TAG_STRING)) {
                continue;
            }

            itemSideModes.put(face, ItemSideMode.valueOf(sideModesTag.getString(face.name())));
        }
        autoInputEnabled = tag.getBoolean("auto_input_enabled");
        autoOutputEnabled = tag.getBoolean("auto_output_enabled");
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
        tickAutomaticItemTransfer(pLevel);

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

    private void tickAutomaticItemTransfer(Level level) {
        if (level.isClientSide || (!autoInputEnabled && !autoOutputEnabled)) {
            autoIOTransferCooldown = 0;
            return;
        }

        if (autoIOTransferCooldown > 0) {
            autoIOTransferCooldown--;
            return;
        }
        autoIOTransferCooldown = AUTO_IO_TRANSFER_INTERVAL - 1;

        for (BlockFace face : BlockFace.values()) {
            ItemSideMode mode = getItemSideMode(face);
            boolean canPullFromFace = autoInputEnabled
                && (mode == ItemSideMode.INPUT || mode == ItemSideMode.BOTH);
            boolean canPushToFace = autoOutputEnabled
                && (mode == ItemSideMode.OUTPUT || mode == ItemSideMode.BOTH);
            if (!canPullFromFace && !canPushToFace) {
                continue;
            }

            Direction direction = getWorldSide(face);
            BlockPos neighborPos = worldPosition.relative(direction);
            if (!level.isLoaded(neighborPos)) {
                continue;
            }

            BlockEntity neighbor = level.getBlockEntity(neighborPos);
            if (neighbor == null || neighbor == this) {
                continue;
            }

            if (canPullFromFace) {
                neighbor.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite())
                    .ifPresent(externalHandler -> transferFirstStack(level, externalHandler, inputOnlyHandler));
            }
            if (canPushToFace) {
                neighbor.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite())
                    .ifPresent(externalHandler -> transferFirstStack(level, outputHandler, externalHandler));
            }
        }
    }

    private void transferFirstStack(Level level, IItemHandler source, IItemHandler destination) {
        for (int sourceSlot = 0; sourceSlot < source.getSlots(); sourceSlot++) {
            ItemStack available = source.getStackInSlot(sourceSlot);
            if (available.isEmpty()) {
                continue;
            }

            ItemStack simulatedRemainder = ItemHandlerHelper.insertItem(destination, available.copy(), true);
            int amountToTransfer = available.getCount() - simulatedRemainder.getCount();
            if (amountToTransfer <= 0) {
                continue;
            }

            ItemStack extracted = source.extractItem(sourceSlot, amountToTransfer, false);
            if (extracted.isEmpty()) {
                continue;
            }

            ItemStack remainder = ItemHandlerHelper.insertItem(destination, extracted, false);
            if (!remainder.isEmpty()) {
                restoreTransferRemainder(level, source, sourceSlot, remainder);
            }
            return;
        }
    }

    private void restoreTransferRemainder(Level level, IItemHandler source, int sourceSlot, ItemStack remainder) {
        if (source == outputHandler) {
            ItemStack currentStack = outputHandler.getStackInSlot(sourceSlot);
            if (currentStack.isEmpty()) {
                outputHandler.setStackInSlot(sourceSlot, remainder);
                return;
            }
            if (ItemHandlerHelper.canItemStacksStack(currentStack, remainder)
                && currentStack.getCount() + remainder.getCount() <= currentStack.getMaxStackSize()) {
                ItemStack restoredStack = currentStack.copy();
                restoredStack.grow(remainder.getCount());
                outputHandler.setStackInSlot(sourceSlot, restoredStack);
                return;
            }
        } else {
            remainder = source.insertItem(sourceSlot, remainder, false);
            if (!remainder.isEmpty()) {
                remainder = ItemHandlerHelper.insertItem(source, remainder, false);
            }
            if (remainder.isEmpty()) {
                return;
            }
        }

        Containers.dropItemStack(
            level,
            worldPosition.getX() + 0.5,
            worldPosition.getY() + 0.5,
            worldPosition.getZ() + 0.5,
            remainder
        );
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

            int remainingAmount = amounts.get(i);

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

        outputSlot = getFirstAvailableOutputSlot(result.getItem(), result.getCount());

        if (outputSlot == -1) {
            return false;
        }

        if (currentRecipe instanceof MachineRecipeWithExtraOutputs extraRecipe) {
            NonNullList<ItemStack> extraOutputs =
                extraRecipe.getExtraOutputs();

            if (extraOutputs != null) {
                for (ItemStack extraOutput : extraOutputs) {
                    if (getFirstAvailableOutputSlot(extraOutput.getItem(), extraOutput.getCount()) == -1) {
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

                    int extraOutputSlot = getFirstAvailableOutputSlot(extraOutput.getItem(), extraOutput.getCount());

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
