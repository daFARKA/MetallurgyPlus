package net.dafarka.metallurgyplus.screen.menu;

import net.dafarka.metallurgyplus.block.entity.multiblock.MBBatteryController;
import net.dafarka.metallurgyplus.block.entity.multiblock.MBReactorController;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MBReactorMenu extends MBBaseMenu {

    private final MBReactorController controller;
    private final Level level;
    private final ContainerData data;

    public MBReactorMenu(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
        this(
            containerId,
            inventory,
            inventory.player.level().getBlockEntity(buffer.readBlockPos()),
            new SimpleContainerData(MBBatteryController.ENERGY_DATA_COUNT)
        );
    }

    public MBReactorMenu(int containerId, Inventory playerInventory, BlockEntity controller, ContainerData data) {
        super(ModMenuTypes.REACTOR_MENU.get(), containerId, 0, data);
        this.controller = (MBReactorController) controller;
        this.level = playerInventory.player.level();
        this.data = data;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(
            ContainerLevelAccess.create(level, controller.getBlockPos()),
            player,
            controller.getBlockState().getBlock()
        );
    }
}
