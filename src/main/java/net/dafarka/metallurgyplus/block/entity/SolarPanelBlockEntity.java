package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.block.custom.SolarPanelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class SolarPanelBlockEntity extends EnergyBlockEntity {

    protected final ContainerData data;

    private int generation = 0;

    public SolarPanelBlockEntity(BlockPos pPos, BlockState pBlockState, int tier) {
        super(
            ModBlockEntities.SOLAR_BLOCK_ENTITIES.get(tier).get(),
            pPos,
            pBlockState,
            getGeneration(tier),
            0,
            getGeneration(tier)
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

    private static int getGeneration(int tier) {
        if (tier == 26) return Integer.MAX_VALUE;

        return SolarPanelBlock.GENERATION * (int) Math.pow(2, tier - 1);
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
}
