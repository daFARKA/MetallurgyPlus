package net.dafarka.metallurgyplus.screen.menu;

import net.dafarka.metallurgyplus.block.entity.multiblock.MBItemPortBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;

public class MBItemPortMenu extends BaseMenu {
    private static final int PORT_SLOT_COUNT = MBItemPortBlockEntity.SLOT_COUNT;

    private final MBItemPortBlockEntity port;
    private final Level level;

    public MBItemPortMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(
            containerId,
            playerInventory,
            (MBItemPortBlockEntity) playerInventory.player.level().getBlockEntity(buffer.readBlockPos())
        );
    }

    public MBItemPortMenu(int containerId, Inventory playerInventory, MBItemPortBlockEntity port) {
        super(ModMenuTypes.MULTIBLOCK_ITEM_PORT_MENU.get(), containerId, PORT_SLOT_COUNT);
        this.port = port;
        this.level = playerInventory.player.level();

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                int slot = row * 3 + column;
                addSlot(new SlotItemHandler(port.getItemHandler(), slot, 62 + column * 18, 17 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return port.isInputPort() && super.mayPlace(stack);
                    }
                });
            }
        }

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
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
