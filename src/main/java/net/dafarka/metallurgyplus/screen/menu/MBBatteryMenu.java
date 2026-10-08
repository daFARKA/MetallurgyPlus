package net.dafarka.metallurgyplus.screen.menu;

import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.entity.multiblock.MBBatteryController;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.math.BigInteger;

public class MBBatteryMenu extends BaseMenu {
    private final MBBatteryController blockEntity;
    private final Level level;
    private final ContainerData data;

    public MBBatteryMenu(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
        this(
            containerId,
            inventory,
            inventory.player.level().getBlockEntity(buffer.readBlockPos()),
            new SimpleContainerData(MBBatteryController.ENERGY_DATA_COUNT)
        );
    }

    public MBBatteryMenu(int containerId, Inventory inventory, BlockEntity blockEntity, ContainerData data) {
        super(ModMenuTypes.MULTIBLOCK_BATTERY_MENU.get(), containerId, 0);
        this.blockEntity = (MBBatteryController) blockEntity;
        this.level = inventory.player.level();
        this.data = data;

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);
        addDataSlots(data);
    }

    public BlockPos getBlockEntityPos() {
        return blockEntity.getBlockPos();
    }

    public BigInteger getEnergyStored() {
        return readEnergyValue(0);
    }

    public BigInteger getMaxEnergy() {
        return readEnergyValue(MBBatteryController.ENERGY_WORDS);
    }

    public boolean isFormed() {
        return data.get(MBBatteryController.ENERGY_WORDS * 2) == 1;
    }

    public int getScaledEnergy() {
        BigInteger maxEnergy = getMaxEnergy();
        BigInteger energy = getEnergyStored();
        if (maxEnergy.signum() <= 0 || energy.signum() <= 0) {
            return 0;
        }

        BigInteger scaled = energy.multiply(BigInteger.valueOf(160)).divide(maxEnergy);
        return scaled.min(BigInteger.valueOf(160)).intValue();
    }

    private BigInteger readEnergyValue(int firstWord) {
        BigInteger value = BigInteger.ZERO;
        for (int word = MBBatteryController.ENERGY_WORDS - 1; word >= 0; word--) {
            value = value.shiftLeft(MBBatteryController.ENERGY_WORD_BITS).or(BigInteger.valueOf(data.get(firstWord + word)));
        }
        return value;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(
            ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
            player,
            ModBlocks.MULTIBLOCKS_MAP.get("battery/controller").get()
        );
    }
}
