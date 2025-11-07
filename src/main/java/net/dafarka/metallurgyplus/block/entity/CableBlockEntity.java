package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.block.GenericEnergyStorage;
import net.dafarka.metallurgyplus.block.custom.CableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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

public class CableBlockEntity extends BlockEntity {

    protected final ContainerData data;

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(CableBlock.TRANSFER * 2, CableBlock.TRANSFER, CableBlock.TRANSFER);
    private final LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);


    public CableBlockEntity(BlockPos pPos,
                              BlockState pBlockState) {
        super(ModBlockEntities.CABLE_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> CableBlockEntity.this.energyStorage.getEnergyStored();
                    case 1 -> CableBlockEntity.this.energyStorage.getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> CableBlockEntity.this.energyStorage.receiveEnergy(pValue, false);
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
                    int energyExtracted = this.energyStorage.extractEnergy(Integer.MAX_VALUE, true);
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
                    int energyPulled = neighborEnergy.extractEnergy(Integer.MAX_VALUE, true);
                    int accepted = this.energyStorage.receiveEnergy(energyPulled, false);
                    neighborEnergy.extractEnergy(accepted, false);
                });
            }
        }

        setChanged(pLevel, pPos, pState);
    }

    public void drops() { }

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
}
