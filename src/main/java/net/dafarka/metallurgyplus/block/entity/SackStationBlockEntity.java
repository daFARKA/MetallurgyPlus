package net.dafarka.metallurgyplus.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SackStationBlockEntity extends BlockEntity {

    protected final ContainerData data;

    private int generation = 0;


    public SackStationBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SACK_STATION_BE.get(), pPos, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {

                }
            }

            @Override
            public int getCount() {
                return 0;
            }
        };
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {

        setChanged(pLevel, pPos, pState);
    }

    public void drops() { }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        return super.getCapability(cap, side);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
    }
}
