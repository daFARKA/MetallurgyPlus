package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.block.custom.SolarPanelBlock;
import net.dafarka.metallurgyplus.energy.BigEnergyStorage;
import net.dafarka.metallurgyplus.energy.IBigEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.math.BigInteger;

public class SolarPanelBlockEntity extends EnergyBlockEntity<BigEnergyStorage> {

    protected final ContainerData data;

    private BigInteger generation;

    public SolarPanelBlockEntity(BlockPos pPos, BlockState pBlockState, int tier) {
        super(
            ModBlockEntities.SOLAR_BLOCK_ENTITIES.get(tier).get(),
            pPos,
            pBlockState,
            () -> new BigEnergyStorage(getGeneration(tier), BigInteger.valueOf(0), getGeneration(tier))
        );

        this.generation = getGeneration(tier);

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

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(
        @NotNull Capability<T> cap,
        @Nullable Direction side
    ) {
        if (cap == ForgeCapabilities.ENERGY) {
            if (side == null || side == Direction.DOWN) {
                return energyLazy.cast();
            }

            return LazyOptional.empty();
        }

        return super.getCapability(cap, side);
    }

    private static BigInteger getGeneration(int tier) {
        return BigInteger.valueOf(SolarPanelBlock.GENERATION).shiftLeft(tier - 1);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (pLevel.isDay() && pLevel.canSeeSky(pPos.above())) {
            energyStorage.generateEnergy(generation);
        }

        BlockPos belowPos = pPos.below();
        if (pLevel.getBlockEntity(belowPos) != null) {
            pLevel.getBlockEntity(belowPos).getCapability(ForgeCapabilities.ENERGY, Direction.UP).ifPresent(storage -> {
                if (storage instanceof IBigEnergyStorage bigStorage) {
                    BigInteger canExtract = energyStorage.extractEnergyBig(generation, true);
                    BigInteger accepted = bigStorage.receiveEnergyBig(canExtract, false);
                    energyStorage.extractEnergyBig(accepted, false);
                } else {
                    BigInteger simulatedExtract = energyStorage.extractEnergyBig(generation, true);
                    int intToSend = simulatedExtract.min(BigInteger.valueOf(Integer.MAX_VALUE)).intValue();

                    if (intToSend > 0) {
                        int accepted = storage.receiveEnergy(intToSend, false);
                        if (accepted > 0) {
                            energyStorage.extractEnergyBig(BigInteger.valueOf(accepted), false);
                        }
                    }
                }
            });
        }

        setChanged(pLevel, pPos, pState);
    }

    public void drops() { }
}
