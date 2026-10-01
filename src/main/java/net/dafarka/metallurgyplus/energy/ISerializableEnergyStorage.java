package net.dafarka.metallurgyplus.energy;

import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;

public interface ISerializableEnergyStorage extends IEnergyStorage, INBTSerializable<Tag> {
}
