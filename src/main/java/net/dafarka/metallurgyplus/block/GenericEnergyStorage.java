package net.dafarka.metallurgyplus.block;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.energy.EnergyStorage;

public class GenericEnergyStorage extends EnergyStorage{
  public GenericEnergyStorage(int capacity, int maxReceive, int maxExtract) {
    super(capacity, maxReceive, maxExtract);
  }

  public void generateEnergy(int amount) {
    this.energy = (int) Math.min((long) this.energy + amount, this.capacity);
  }

  @Override
  public CompoundTag serializeNBT() {
    CompoundTag tag = new CompoundTag();
    tag.putInt("Energy", this.energy);
    return tag;
  }

  @Override
  public void deserializeNBT(Tag nbt) {
    if (nbt instanceof CompoundTag) {
      this.energy = ((CompoundTag) nbt).getInt("Energy");
    }
  }
}
