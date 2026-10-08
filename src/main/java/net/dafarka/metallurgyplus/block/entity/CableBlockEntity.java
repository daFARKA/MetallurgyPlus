package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.block.custom.CableBlock;
import net.dafarka.metallurgyplus.block.entity.base.EnergyBlockEntity;
import net.dafarka.metallurgyplus.energy.BigEnergyStorage;
import net.dafarka.metallurgyplus.energy.IBigEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class CableBlockEntity extends EnergyBlockEntity<BigEnergyStorage> {

    protected final ContainerData data;

    public CableBlockEntity(BlockPos pPos, BlockState pBlockState, int tier) {
        super(
            ModBlockEntities.CABLE_BLOCK_ENTITIES.get(tier).get(),
            pPos,
            pBlockState,
            () -> new BigEnergyStorage(getTransfer(tier))
        );

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> energyStorage.getEnergyStored();
                    case 1 -> energyStorage.getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> energyStorage.receiveEnergy(pValue, false);
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public static BigInteger getTransfer(int tier) {
        return BigInteger.valueOf(CableBlock.TRANSFER).multiply(BigInteger.TEN.pow(tier - 1));
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        Map<IEnergyStorage, Direction> receivers = new HashMap<>();
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pPos.relative(direction);
            BlockEntity neighbor = pLevel.getBlockEntity(neighborPos);
            if (neighbor != null) {
                neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(cap -> {
                    if (cap.canReceive()) {
                        receivers.put(cap, direction);
                    }
                });
            }
        }

        if (receivers.isEmpty()) return;

        BigInteger energyAvailable = energyStorage.getEnergyStoredBig();
        if (energyAvailable.signum() <= 0) return;

        BigInteger[] share = energyAvailable.divideAndRemainder(BigInteger.valueOf(receivers.size()));
        int remainder = share[1].intValue();
        int receiverIndex = 0;

        for (IEnergyStorage receiver : receivers.keySet()) {
            BigInteger offered = share[0];
            if (receiverIndex < remainder) {
                offered = offered.add(BigInteger.ONE);
            }

            BigInteger accepted = receiveEnergy(receiver, offered);
            if (accepted.signum() > 0) {
                energyStorage.extractEnergyBig(accepted, false);
            }
            receiverIndex++;
        }

        setChanged(pLevel, pPos, pState);
    }

    private static BigInteger receiveEnergy(IEnergyStorage receiver, BigInteger offered) {
        if (offered.signum() <= 0) {
            return BigInteger.ZERO;
        }

        BigInteger accepted;
        BigInteger acceptanceLimit = offered;
        if (receiver instanceof IBigEnergyStorage bigReceiver) {
            accepted = bigReceiver.receiveEnergyBig(offered, false);
        } else {
            BigInteger standardOfferBig = offered.min(BigInteger.valueOf(Integer.MAX_VALUE));
            int standardOffer = standardOfferBig.intValue();
            accepted = BigInteger.valueOf(receiver.receiveEnergy(standardOffer, false));
            acceptanceLimit = standardOfferBig;
        }

        return accepted.max(BigInteger.ZERO).min(acceptanceLimit);
    }

    public void drops() { }
}
