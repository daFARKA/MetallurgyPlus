package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.block.entity.base.EnergyBlockEntity;
import net.dafarka.metallurgyplus.energy.GenericEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class PowerSourceBlockEntity extends EnergyBlockEntity<GenericEnergyStorage> {

    public PowerSourceBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(
            ModBlockEntities.POWER_SOURCE_BE.get(),
            pPos,
            pBlockState,
            Integer.MAX_VALUE,
            0,
            Integer.MAX_VALUE
        );
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        energyStorage.generateEnergy(Integer.MAX_VALUE);

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pPos.relative(direction);
            BlockEntity neighbor = pLevel.getBlockEntity(neighborPos);

            if (neighbor != null) {
                neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(target -> {
                    int energyExtracted = energyStorage.extractEnergy(Integer.MAX_VALUE, true);
                    int accepted = target.receiveEnergy(energyExtracted, false);
                    //energyStorage.extractEnergy(accepted, false);
                });
            }
        }

        setChanged(pLevel, pPos, pState);
    }

    public void drops() { }
}
