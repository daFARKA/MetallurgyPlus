package net.dafarka.metallurgyplus.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.math.BigInteger;

public class BigEnergyStorage implements IBigEnergyStorage, ISerializableEnergyStorage {
    protected BigInteger energy;
    protected BigInteger capacity;
    protected BigInteger maxReceive;
    protected BigInteger maxExtract;

    public BigEnergyStorage(BigInteger capacity, BigInteger maxReceive, BigInteger maxExtract) {
        this.capacity = capacity != null ? capacity : BigInteger.ZERO;
        this.energy = BigInteger.ZERO;
        this.maxReceive = maxReceive != null ? maxReceive : BigInteger.ZERO;
        this.maxExtract = maxExtract != null ? maxExtract : BigInteger.ZERO;
    }

    public BigEnergyStorage(BigInteger capacity, BigInteger transfer) {
        this(capacity, transfer, transfer);
    }

    public BigEnergyStorage(BigInteger capacity) {
        this(capacity, capacity, capacity);
    }

    public boolean setLimits(BigInteger capacity, BigInteger maxReceive, BigInteger maxExtract) {
        BigInteger nextCapacity = nonNegative(capacity);
        BigInteger nextMaxReceive = nonNegative(maxReceive);
        BigInteger nextMaxExtract = nonNegative(maxExtract);
        if (this.capacity.equals(nextCapacity)
            && this.maxReceive.equals(nextMaxReceive)
            && this.maxExtract.equals(nextMaxExtract)) {
            return false;
        }

        this.capacity = nextCapacity;
        this.maxReceive = nextMaxReceive;
        this.maxExtract = nextMaxExtract;
        return true;
    }

    public boolean setLimits(BigInteger capacity, BigInteger transfer) {
        return setLimits(capacity, transfer, transfer);
    }

    public boolean setLimits(BigInteger capacity) {
        return setLimits(capacity, capacity, capacity);
    }

    private static BigInteger nonNegative(BigInteger value) {
        return value == null ? BigInteger.ZERO : value.max(BigInteger.ZERO);
    }

    @Override
    public void generateEnergy(BigInteger generation) {
        this.energy = this.energy.add(generation);
    }

    @Override
    public BigInteger receiveEnergyBig(BigInteger maxReceive, boolean simulate) {
        if (!canReceive() || maxReceive.compareTo(BigInteger.ZERO) <= 0) {
            return BigInteger.ZERO;
        }

        BigInteger spaceRemaining = this.capacity.subtract(this.energy);
        BigInteger energyReceived = spaceRemaining.min(this.maxReceive).min(maxReceive);

        if (energyReceived.compareTo(BigInteger.ZERO) <= 0) {
            return BigInteger.ZERO;
        }

        if (!simulate) {
            this.energy = this.energy.add(energyReceived);
        }
        return energyReceived;
    }

    @Override
    public BigInteger extractEnergyBig(BigInteger maxExtract, boolean simulate) {
        if (!canExtract() || maxExtract.compareTo(BigInteger.ZERO) <= 0) {
            return BigInteger.ZERO;
        }

        BigInteger energyExtracted = this.energy.min(this.maxExtract).min(maxExtract);

        if (energyExtracted.compareTo(BigInteger.ZERO) <= 0) {
            return BigInteger.ZERO;
        }

        if (!simulate) {
            this.energy = this.energy.subtract(energyExtracted);
        }
        return energyExtracted;
    }

    @Override
    public BigInteger getEnergyStoredBig() {
        return this.energy;
    }

    @Override
    public BigInteger getMaxEnergyStoredBig() {
        return this.capacity;
    }

    public BigInteger getMaxReceiveBig() {
        return this.maxReceive;
    }

    public BigInteger getMaxExtractBig() {
        return this.maxExtract;
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract.compareTo(BigInteger.ZERO) > 0;
    }

    @Override
    public boolean canReceive() {
        return this.maxReceive.compareTo(BigInteger.ZERO) > 0;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putByteArray("Energy", this.energy.toByteArray());
        tag.putByteArray("Capacity", this.capacity.toByteArray());
        return tag;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        if (nbt instanceof CompoundTag tag) {
            if (tag.contains("Energy")) {
                this.energy = new BigInteger(tag.getByteArray("Energy"));
            } else {
                this.energy = BigInteger.ZERO;
            }

            if (tag.contains("Capacity")) {
                this.capacity = new BigInteger(tag.getByteArray("Capacity"));
            } else {
                this.capacity = BigInteger.ZERO;
            }
        }
    }
}
