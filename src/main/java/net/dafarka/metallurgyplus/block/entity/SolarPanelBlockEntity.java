package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.GenericEnergyStorage;
import net.dafarka.metallurgyplus.block.custom.SolarPanelBlock;
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

public class SolarPanelBlockEntity extends BlockEntity {

    protected final ContainerData data;

    private final GenericEnergyStorage energyStorage;
    private final LazyOptional<IEnergyStorage> energy;

    private int generation = 0;


    public SolarPanelBlockEntity(BlockPos pPos, BlockState pBlockState, int tier) {
        super(ModBlockEntities.SOLAR_BLOCK_ENTITIES.get(tier).get(), pPos, pBlockState);
        if (tier <= 0) {
            throw new IllegalArgumentException("Tier must be greater than 0! Found: " + tier);
        }

        this.generation = SolarPanelBlock.GENERATION * (int) Math.pow(2, tier - 1);
        if (tier == 26) generation = Integer.MAX_VALUE;
        this.energyStorage = new GenericEnergyStorage(generation, 0, generation);
        this.energy = LazyOptional.of(() -> energyStorage);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> SolarPanelBlockEntity.this.energyStorage.getEnergyStored();
                    case 1 -> SolarPanelBlockEntity.this.energyStorage.getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> SolarPanelBlockEntity.this.energyStorage.receiveEnergy(pValue, false);
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (pLevel.isDay() && pLevel.canSeeSky(pPos.above())) {
            energyStorage.generateEnergy(generation);
        }

        BlockPos belowPos = pPos.below();
        if (pLevel.getBlockEntity(belowPos) != null) {
            pLevel.getBlockEntity(belowPos).getCapability(ForgeCapabilities.ENERGY, null).ifPresent(storage -> {
                int energyToSend = energyStorage.extractEnergy(generation, true);
                int accepted = storage.receiveEnergy(energyToSend, false);
                energyStorage.extractEnergy(accepted, false);
            });
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
