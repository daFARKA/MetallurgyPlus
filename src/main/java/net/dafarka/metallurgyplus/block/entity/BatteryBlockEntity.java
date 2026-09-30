package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.block.custom.BatteryBlock;
import net.dafarka.metallurgyplus.screen.menu.BatteryMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nullable;

public class BatteryBlockEntity extends EnergyBlockEntity implements MenuProvider {

    protected final ContainerData data;

    public BatteryBlockEntity(BlockPos pPos, BlockState pBlockState, int tier) {
        super(
            ModBlockEntities.BATTERY_BLOCK_ENTITIES.get(tier).get(),
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
                    case 2 -> tier;
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

        double transfer_d = BatteryBlock.CAPACITY * Math.pow(10, tier - 2);

        return (int) transfer_d;
    }

    private static int getCapacity(int tier) {
        if (tier == 7) return Integer.MAX_VALUE;

        return BatteryBlock.CAPACITY * (int) Math.pow(10, tier - 1);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        // Try sending energy to neighbors
        for (Direction direction : Direction.values()) {
            BlockEntity neighbor = pLevel.getBlockEntity(pPos.relative(direction));
            if (neighbor != null) {
                neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(neighborEnergy -> {
                    int energyExtracted = this.energyStorage.extractEnergy(10000, true);
                    int energyReceived = neighborEnergy.receiveEnergy(energyExtracted, false);
                    this.energyStorage.extractEnergy(energyReceived, false);
                });
            }
        }

        // Try pulling energy from neighbors
        for (Direction direction : Direction.values()) {
            BlockEntity neighbor = pLevel.getBlockEntity(pPos.relative(direction));
            if (neighbor != null) {
                neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(neighborEnergy -> {
                    int energyPulled = neighborEnergy.extractEnergy(10000, true);
                    int accepted = this.energyStorage.receiveEnergy(energyPulled, false);
                    neighborEnergy.extractEnergy(accepted, false);
                });
            }
        }

        setChanged(pLevel, pPos, pState);
    }

    public void saveToItem(CompoundTag tag) {
        CompoundTag energyTag = this.energyStorage.serializeNBT();
        tag.put("energy", energyTag);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.metallurgyplus.battery");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new BatteryMenu(pContainerId, pPlayerInventory, this, this.data);
    }
}
