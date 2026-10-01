package net.dafarka.metallurgyplus.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.energy.EnergyStorage;

public class GenericEnergyStorage extends EnergyStorage implements ISerializableEnergyStorage {
    public GenericEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public void generateEnergy(int amount) {
        this.energy = (int) Math.min((long) this.energy + amount, (long) this.capacity);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("energy", this.energy);
        return tag;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        if (nbt instanceof CompoundTag) {
            this.energy = ((CompoundTag) nbt).getInt("energy");
        }
    }
}
