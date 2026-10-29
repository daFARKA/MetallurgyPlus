package net.dafarka.metallurgyplus.screen.menu;

import net.dafarka.metallurgyplus.block.entity.multiblock.MBReactorController;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;

public class ReactorMenu extends BaseMenu {

    private final MBReactorController controller;
    private final Level level;

    public ReactorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(
            containerId,
            playerInventory,
            (MBReactorController) playerInventory.player.level().getBlockEntity(buffer.readBlockPos())
        );
    }

    public ReactorMenu(int containerId, Inventory playerInventory, MBReactorController controller) {
        super(ModMenuTypes.REACTOR_MENU.get(), containerId, 0);
        this.controller = controller;
        this.level = playerInventory.player.level();

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
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
