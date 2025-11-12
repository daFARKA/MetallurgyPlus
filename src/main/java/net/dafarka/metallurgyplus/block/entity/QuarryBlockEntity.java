package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.block.GenericEnergyStorage;
import net.dafarka.metallurgyplus.screen.menu.QuarryMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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

import java.util.Optional;

public class QuarryBlockEntity extends BlockEntity implements MenuProvider {
    public static final int ENERGY_CONSUMPTION = 400;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;
    private BlockPos targetStart = null;
    private BlockPos targetEnd = null;
    private int currentX, currentY, currentZ;
    private boolean running = false;
    private boolean areaLocked = false;

    private int tempStartX, tempStartY, tempStartZ;
    private int tempEndX, tempEndY, tempEndZ;

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
    private LazyOptional<GenericEnergyStorage> energyLazy = LazyOptional.empty();

    public QuarryBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.QUARRY_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> QuarryBlockEntity.this.progress;
                    case 1 -> QuarryBlockEntity.this.maxProgress;
                    case 2 -> QuarryBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> QuarryBlockEntity.this.energyStorage.getMaxEnergyStored();
                    case 4 -> QuarryBlockEntity.this.areaLocked ? 1 : 0;
                    case 5 -> QuarryBlockEntity.this.targetStart != null ? QuarryBlockEntity.this.targetStart.getX() : 0;
                    case 6 -> QuarryBlockEntity.this.targetStart != null ? QuarryBlockEntity.this.targetStart.getY() : 0;
                    case 7 -> QuarryBlockEntity.this.targetStart != null ? QuarryBlockEntity.this.targetStart.getZ() : 0;
                    case 8 -> QuarryBlockEntity.this.targetEnd != null ? QuarryBlockEntity.this.targetEnd.getX() : 0;
                    case 9 -> QuarryBlockEntity.this.targetEnd != null ? QuarryBlockEntity.this.targetEnd.getY() : 0;
                    case 10 -> QuarryBlockEntity.this.targetEnd != null ? QuarryBlockEntity.this.targetEnd.getZ() : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> QuarryBlockEntity.this.progress = pValue;
                    case 1 -> QuarryBlockEntity.this.maxProgress = pValue;
                    case 2 -> QuarryBlockEntity.this.energyStorage.receiveEnergy(pValue, false);
                }
            }

            @Override
            public int getCount() {
                return 11;
            }
        };
    }

    private final ItemStackHandler outputHandler = new ItemStackHandler(9);
    private LazyOptional<IItemHandler> outputLazy = LazyOptional.empty();

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return outputLazy.cast();
        } else if (cap == ForgeCapabilities.ENERGY) {
            return energyLazy.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        outputLazy = LazyOptional.of(() -> outputHandler);

        energyLazy = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        outputLazy.invalidate();

        energyLazy.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(outputHandler.getSlots());
        for (int i = 0; i < outputHandler.getSlots(); i++) {
            inventory.setItem(i, outputHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.metallurgyplus.quarry");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new QuarryMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("output", outputHandler.serializeNBT());
        pTag.putInt("progress", progress);

        pTag.put("energy", energyStorage.serializeNBT());

        if (targetStart != null) pTag.putLong("targetStart", targetStart.asLong());
        if (targetEnd != null) pTag.putLong("targetEnd", targetEnd.asLong());
        pTag.putInt("progress", progress);
        pTag.putInt("cx", currentX);
        pTag.putInt("cy", currentY);
        pTag.putInt("cz", currentZ);
        pTag.putBoolean("areaLocked", areaLocked);

        pTag.putInt("tempStartX", tempStartX);
        pTag.putInt("tempStartY", tempStartY);
        pTag.putInt("tempStartZ", tempStartZ);
        pTag.putInt("tempEndX", tempEndX);
        pTag.putInt("tempEndY", tempEndY);
        pTag.putInt("tempEndZ", tempEndZ);

        pTag.putBoolean("running", running);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        outputHandler.deserializeNBT(pTag.getCompound("output"));
        progress = pTag.getInt("progress");

        energyStorage.deserializeNBT(pTag.getCompound("energy"));

        if (pTag.contains("targetStart")) targetStart = BlockPos.of(pTag.getLong("targetStart"));
        if (pTag.contains("targetEnd")) targetEnd = BlockPos.of(pTag.getLong("targetEnd"));
        progress = pTag.getInt("progress");
        currentX = pTag.getInt("cx");
        currentY = pTag.getInt("cy");
        currentZ = pTag.getInt("cz");
        areaLocked = pTag.getBoolean("areaLocked");

        tempStartX = pTag.getInt("tempStartX");
        tempStartY = pTag.getInt("tempStartY");
        tempStartZ = pTag.getInt("tempStartZ");
        tempEndX = pTag.getInt("tempEndX");
        tempEndY = pTag.getInt("tempEndY");
        tempEndZ = pTag.getInt("tempEndZ");

        running = pTag.getBoolean("running");

        if (targetStart != null && targetEnd != null) {
            calculateMinsAndMaxs(targetStart, targetEnd);
        }
    }

    private void calculateMinsAndMaxs(BlockPos targetStart, BlockPos targetEnd) {
        minX = Math.min(targetStart.getX(), targetEnd.getX());
        maxX = Math.max(targetStart.getX(), targetEnd.getX());
        minY = Math.min(targetStart.getY(), targetEnd.getY());
        maxY = Math.max(targetStart.getY(), targetEnd.getY());
        minZ = Math.min(targetStart.getZ(), targetEnd.getZ());
        maxZ = Math.max(targetStart.getZ(), targetEnd.getZ());
    }

    private int minX, maxX, minY, maxY, minZ, maxZ;
    public void setTargetArea(BlockPos start, BlockPos end) {
        this.targetStart = start;
        this.targetEnd = end;
        this.areaLocked = true;

        calculateMinsAndMaxs(start, end);

        currentX = minX;
        currentY = maxY;
        currentZ = minZ;

        setChanged();
    }

    public void startOperation() {
        if (targetStart != null && targetEnd != null) {
            running = true;
            setChanged();
        }
    }

    public void stopOperation() {
        running = false;
        setChanged();
    }

    public void toggleOperation() {
        if (targetStart != null && targetEnd != null) {
            running = !running;
            setChanged();
        }
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (!(pLevel instanceof ServerLevel serverLevel)) return;
        if (!running) return;
        if (targetStart == null || targetEnd == null) return;

        pushInternalToExternal(serverLevel);
        if (energyStorage.getEnergyStored() >= ENERGY_CONSUMPTION) {
            if (canMine(serverLevel)) {
                progress++;
                if (progress >= 1) {
                    progress = 0;
                    mineNextBlock(serverLevel);
                }
            }
        }
    }

    private void pushInternalToExternal(Level level) {
        BlockPos abovePos = worldPosition.above();
        var blockEntity = level.getBlockEntity(abovePos);
        if (blockEntity == null) return;

        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.DOWN).ifPresent(externalHandler -> {
            for (int i = 0; i < outputHandler.getSlots(); i++) {
                ItemStack stack = outputHandler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    ItemStack remainder = ItemHandlerHelper.insertItem(externalHandler, stack.copy(), false);
                    outputHandler.setStackInSlot(i, remainder);
                }
            }
        });
    }

    private boolean canMine(ServerLevel level) {
        BlockPos current = new BlockPos(currentX, currentY, currentZ);
        BlockState state = level.getBlockState(current);

        if (!state.isAir() && state.getDestroySpeed(level, current) >= 0) {
            var drops = Block.getDrops(state, level, current, null);
            for (ItemStack drop : drops) {
                if (!canInsertStack(drop)) return false;
            }
        }

        return true;
    }

    private void mineNextBlock(ServerLevel level) {
        BlockPos current = new BlockPos(currentX, currentY, currentZ);
        BlockState state = level.getBlockState(current);

        if (!state.isAir() && state.getDestroySpeed(level, current) >= 0) {
            var drops = Block.getDrops(state, level, current, null);
            for (ItemStack drop : drops) {
                ItemHandlerHelper.insertItem(outputHandler, drop, false);
            }
            level.destroyBlock(current, false);
        }

        advancePosition();
    }

    private void advancePosition() {
        currentX++;
        if (currentX > maxX) {
            currentX = minX;
            currentZ++;
            if (currentZ > maxZ) {
                currentZ = minZ;
                currentY--; // decrement to go top → bottom
                if (currentY < minY) {
                    running = false; // finished
                }
            }
        }
    }

    private boolean canInsertStack(ItemStack stack) {
        for (int i = 0; i < outputHandler.getSlots(); i++) {
            ItemStack slotStack = outputHandler.getStackInSlot(i);

            if (slotStack.isEmpty() || (ItemStack.isSameItemSameTags(slotStack, stack)
                && slotStack.getCount() + stack.getCount() <= slotStack.getMaxStackSize())) {
                return true;
            }
        }

        return false;
    }

    public void setTempStart(int x, int y, int z) { tempStartX = x; tempStartY = y; tempStartZ = z; setChanged(); }
    public void setTempEnd(int x, int y, int z) { tempEndX = x; tempEndY = y; tempEndZ = z; setChanged(); }

    public int getTempStartX() { return tempStartX; }
    public int getTempStartY() { return tempStartY; }
    public int getTempStartZ() { return tempStartZ; }

    public int getTempEndX() { return tempEndX; }
    public int getTempEndY() { return tempEndY; }
    public int getTempEndZ() { return tempEndZ; }

    public boolean isRunning() { return running; }
}
