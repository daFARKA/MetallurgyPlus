package net.dafarka.metallurgyplus.block;

import net.minecraftforge.energy.EnergyStorage;

public class GenericEnergyStorage extends  EnergyStorage{
  public GenericEnergyStorage(int capacity) {
    super(capacity);
  }

  public GenericEnergyStorage(int capacity, int maxTransfer) {
    super(capacity, maxTransfer);
  }

  public GenericEnergyStorage(int capacity, int maxReceive, int maxExtract) {
    super(capacity, maxReceive, maxExtract);
  }

  public GenericEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
    super(capacity, maxReceive, maxExtract, energy);
  }

  public void generateEnergy(int amount) {
    this.energy = (int) Math.min((long) this.energy + amount, this.capacity);
  }
}
