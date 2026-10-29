package net.dafarka.metallurgyplus.screen.menu;

import net.dafarka.metallurgyplus.block.entity.multiblock.MBFluidPortBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public class MBFluidPortMenu extends BaseMenu {

    private final MBFluidPortBlockEntity port;
    private final Level level;

    public MBFluidPortMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(
            containerId,
            playerInventory,
            (MBFluidPortBlockEntity) playerInventory.player.level().getBlockEntity(buffer.readBlockPos())
        );
    }

    public MBFluidPortMenu(int containerId, Inventory playerInventory, MBFluidPortBlockEntity port) {
        super(ModMenuTypes.MULTIBLOCK_FLUID_PORT_MENU.get(), containerId, 0);
        this.port = port;
        this.level = playerInventory.player.level();

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    public int getFluidStored() {
        return port.getFluidStored();
    }

    public int getFluidCapacity() {
        return port.getFluidCapacity();
    }

    public FluidStack getFluid() {
        return port.getFluid();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(
            ContainerLevelAccess.create(level, port.getBlockPos()),
            player,
            port.getBlockState().getBlock()
        );
    }
}
