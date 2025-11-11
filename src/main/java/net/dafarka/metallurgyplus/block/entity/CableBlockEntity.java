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
import java.util.HashMap;
import java.util.Map;

public class CableBlockEntity extends BlockEntity {

    protected final ContainerData data;

    private final GenericEnergyStorage energyStorage;
    private final LazyOptional<IEnergyStorage> energy;


    public CableBlockEntity(BlockPos pPos, BlockState pBlockState, int tier) {
        super(ModBlockEntities.CABLE_BLOCK_ENTITIES.get(tier).get(), pPos, pBlockState);
        if (tier <= 0) {
            throw new IllegalArgumentException("Tier must be greater than 0! Found: " + tier);
        }

        int transfer = CableBlock.TRANSFER * (int) Math.pow(10, tier - 1);
        if (tier == 8) transfer = Integer.MAX_VALUE;
        this.energyStorage = new GenericEnergyStorage(transfer, transfer, transfer);
        this.energy = LazyOptional.of(() -> energyStorage);

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
        Map<IEnergyStorage, Direction> receivers = new HashMap<>();
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pPos.relative(direction);
            BlockEntity neighbor = pLevel.getBlockEntity(neighborPos);
            if (neighbor != null) {
                neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(cap -> {
                    if (cap.canReceive()) {
                        receivers.put(cap, direction);
                    }
                });
            }
        }

        if (receivers.isEmpty()) return;

        int energyAvailable = energyStorage.getEnergyStored();
        int energyPerReceiver = energyAvailable / receivers.size();

        for (IEnergyStorage receiver : receivers.keySet()) {
            int accepted = receiver.receiveEnergy(energyPerReceiver, false);
            energyStorage.extractEnergy(accepted, false);
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
