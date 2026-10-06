package net.dafarka.metallurgyplus.screen.menu;

import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.entity.BatteryBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;


public class BatteryMenu extends BaseMenu {

    public UtilityMenu utilityMenu;

    public final BatteryBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public BatteryMenu(int pContainerId, Inventory inv, FriendlyByteBuf friendlyByteBuf) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(friendlyByteBuf.readBlockPos()), new SimpleContainerData(19));
    }

    public BatteryMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.BATTERY_MENU.get(), pContainerId, 0);
        blockEntity = ((BatteryBlockEntity) entity);
        this.level = inv.player.level();
        this.data = data;
        this.utilityMenu = new UtilityMenu(this.data);

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
            pPlayer, ModBlocks.BATTERY_BLOCK_MAP.get(this.data.get(2)).get());
    }

    public int getScaledEnergy() {
        int energy = this.data.get(0);
        int maxEnergy = this.data.get(1);
        int energyBarSize = 160;

        if (maxEnergy <= 0 || energy <= 0) {
            return 0;
        }

        double scaled = ((double) energy / maxEnergy) * energyBarSize;

        return (int) Math.round(scaled);
    }

    public int getEnergyStored() {
        return this.data.get(0);
    }

    public int getMaxEnergy() {
        return this.data.get(1);
    }
}
