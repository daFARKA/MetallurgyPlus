package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.GenericEnergyStorage;
import net.dafarka.metallurgyplus.block.custom.BatteryBlock;
import net.dafarka.metallurgyplus.block.custom.CableBlock;
import net.dafarka.metallurgyplus.screen.menu.BatteryMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BatteryBlockEntity extends BlockEntity implements MenuProvider {

    protected final ContainerData data;

    private final GenericEnergyStorage energyStorage;
    private final LazyOptional<IEnergyStorage> energy;

    private int tier = 0;

    public BatteryBlockEntity(BlockPos pPos, BlockState pBlockState, int tier) {
        super(ModBlockEntities.BATTERY_BLOCK_ENTITIES.get(tier).get(), pPos, pBlockState);
        if (tier <= 0) {
            throw new IllegalArgumentException("Tier must be greater than 0! Found: " + tier);
        }

        this.tier = tier;
        int capacity = BatteryBlock.CAPACITY * (int) Math.pow(10, tier - 1);
        double transfer_d = BatteryBlock.CAPACITY * Math.pow(10, tier - 2);
        int transfer = (int) transfer_d;
        if (tier == 7) capacity = Integer.MAX_VALUE;
        this.energyStorage = new GenericEnergyStorage(capacity, transfer, transfer);
        this.energy = LazyOptional.of(() -> energyStorage);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> BatteryBlockEntity.this.energyStorage.getEnergyStored();
                    case 1 -> BatteryBlockEntity.this.energyStorage.getMaxEnergyStored();
                    case 2 -> BatteryBlockEntity.this.tier;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> BatteryBlockEntity.this.energyStorage.receiveEnergy(pValue, false);
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        // Try sending energy to neighbors
        for (Direction direction : Direction.values()) {
            BlockEntity neighbor = pLevel.getBlockEntity(pPos.relative(direction));
            if (neighbor != null) {
                neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(neighborEnergy -> {
                    int energyExtracted = this.energyStorage.extractEnergy(10000, true);
                    int energyReceived = neighborEnergy.receiveEnergy(energyExtracted, false);
                    this.energyStorage.extractEnergy(energyReceived, false);
                });
            }
        }

        // Try pulling energy from neighbors
        for (Direction direction : Direction.values()) {
            BlockEntity neighbor = pLevel.getBlockEntity(pPos.relative(direction));
            if (neighbor != null) {
                neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(neighborEnergy -> {
                    int energyPulled = neighborEnergy.extractEnergy(10000, true);
                    int accepted = this.energyStorage.receiveEnergy(energyPulled, false);
                    neighborEnergy.extractEnergy(accepted, false);
                });
            }
        }

        setChanged(pLevel, pPos, pState);
    }

    public void saveToItem(CompoundTag tag) {
        CompoundTag energyTag = this.energyStorage.serializeNBT();
        tag.put("energy", energyTag);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energy.invalidate();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energy.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("energy", energyStorage.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        energyStorage.deserializeNBT(pTag.getCompound("energy"));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.metallurgyplus.battery");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new BatteryMenu(pContainerId, pPlayerInventory, this, this.data);
    }
}
