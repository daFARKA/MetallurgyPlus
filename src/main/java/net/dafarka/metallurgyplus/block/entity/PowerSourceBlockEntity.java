package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.GenericEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class PowerSourceBlockEntity extends BlockEntity {
  private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
  private final LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);


  public PowerSourceBlockEntity(BlockPos pPos,
                                BlockState pBlockState) {
    super(ModBlockEntities.POWER_SOURCE_BE.get(), pPos, pBlockState);
  }

  public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
    energyStorage.generateEnergy(Integer.MAX_VALUE);

    for (Direction dir : Direction.values()) {
      BlockPos neighborPos = pPos.relative(dir);
      BlockEntity neighbor = pLevel.getBlockEntity(neighborPos);

      if (neighbor != null) {
        neighbor.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).ifPresent(target -> {
          int energyExtracted = energyStorage.extractEnergy(Integer.MAX_VALUE, true);
          int accepted = target.receiveEnergy(energyExtracted, false);
          energyStorage.extractEnergy(accepted, false);
        });
      }
    }

    setChanged(pLevel, pPos, pState);
  }

  public void drops() {
    // TODO: drop logic??
  }

  @Override
  public void invalidateCaps() {
    super.invalidateCaps();
    energy.invalidate();
  }

  @Override
  public void reviveCaps() {
    super.reviveCaps();
    // Recreate the LazyOptional if needed
  }

  @NotNull
  @Override
  public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
    if (cap == ForgeCapabilities.ENERGY) {
      return energy.cast();
    }
    return super.getCapability(cap, side);
  }
}
