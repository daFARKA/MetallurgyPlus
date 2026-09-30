package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.block.custom.CableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.HashMap;
import java.util.Map;

public class CableBlockEntity extends EnergyBlockEntity {

    protected final ContainerData data;

    public CableBlockEntity(BlockPos pPos, BlockState pBlockState, int tier) {
        super(
            ModBlockEntities.CABLE_BLOCK_ENTITIES.get(tier).get(),
            pPos,
            pBlockState,
            getCapacity(tier),
            getTransfer(tier),
            getTransfer(tier)
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

    private static int getTransfer(int tier) {
        if (tier == 8) return Integer.MAX_VALUE;

        return CableBlock.TRANSFER * (int) Math.pow(10, tier - 1);
    }

    private static int getCapacity(int tier) {
        return getTransfer(tier);
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

        int energyAvailable = energyStorage.getEnergyStored();
        int energyPerReceiver = energyAvailable / receivers.size();

        for (IEnergyStorage receiver : receivers.keySet()) {
            int accepted = receiver.receiveEnergy(energyPerReceiver, false);
            energyStorage.extractEnergy(accepted, false);
        }

        setChanged(pLevel, pPos, pState);
    }

    public void drops() { }
}
