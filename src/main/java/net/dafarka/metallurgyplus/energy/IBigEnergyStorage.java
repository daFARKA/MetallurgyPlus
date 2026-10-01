package net.dafarka.metallurgyplus.energy;

import net.minecraftforge.energy.IEnergyStorage;

import java.math.BigInteger;

public interface IBigEnergyStorage extends IEnergyStorage {
    BigInteger getEnergyStoredBig();

    BigInteger getMaxEnergyStoredBig();

    BigInteger receiveEnergyBig(BigInteger maxReceive, boolean simulate);

    BigInteger extractEnergyBig(BigInteger maxExtract, boolean simulate);

    void generateEnergy(BigInteger generation);

    // Standard IEnergyStorage clamping fallbacks for 3rd-party compatibility
    @Override
    default int getEnergyStored() {
        BigInteger stored = getEnergyStoredBig();
        return stored.min(BigInteger.valueOf(Integer.MAX_VALUE)).intValue();
    }

    @Override
    default int getMaxEnergyStored() {
        BigInteger max = getMaxEnergyStoredBig();
        return max.min(BigInteger.valueOf(Integer.MAX_VALUE)).intValue();
    }

    @Override
    default int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive <= 0) return 0;
        BigInteger received = receiveEnergyBig(BigInteger.valueOf(maxReceive), simulate);
        return received.min(BigInteger.valueOf(Integer.MAX_VALUE)).intValue();
    }

    @Override
    default int extractEnergy(int maxExtract, boolean simulate) {
        if (maxExtract <= 0) return 0;
        BigInteger extracted = extractEnergyBig(BigInteger.valueOf(maxExtract), simulate);
        return extracted.min(BigInteger.valueOf(Integer.MAX_VALUE)).intValue();
    }
}