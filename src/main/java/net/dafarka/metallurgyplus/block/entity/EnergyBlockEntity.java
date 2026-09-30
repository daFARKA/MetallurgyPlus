package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.block.GenericEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class EnergyBlockEntity extends BaseBlockEntity {

    protected final GenericEnergyStorage energyStorage;
    protected LazyOptional<IEnergyStorage> energyLazy = LazyOptional.empty();

    protected EnergyBlockEntity(
        BlockEntityType<?> type,
        BlockPos pos,
        BlockState state,
        int capacity,
        int maxReceive,
        int maxExtract
    ) {
        super(type, pos, state);

        this.energyStorage = new GenericEnergyStorage(
            capacity,
            maxReceive,
            maxExtract
        );
    }

    @Override
    public void onLoad() {
        super.onLoad();

        energyLazy = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        energyLazy.invalidate();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(
        @NotNull Capability<T> cap,
        @Nullable net.minecraft.core.Direction side
    ) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyLazy.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put("energy", energyStorage.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        energyStorage.deserializeNBT(tag.getCompound("energy"));
    }
}
